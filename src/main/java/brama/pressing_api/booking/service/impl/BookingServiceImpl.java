package brama.pressing_api.booking.service.impl;

import brama.pressing_api.booking.BookingMapper;
import brama.pressing_api.booking.domain.model.Booking;
import brama.pressing_api.booking.domain.model.BookingPaymentStatus;
import brama.pressing_api.booking.domain.model.BookingPricing;
import brama.pressing_api.booking.domain.model.BookingStatus;
import brama.pressing_api.booking.dto.request.CreateBookingRequest;
import brama.pressing_api.booking.dto.response.BookingResponse;
import brama.pressing_api.booking.repo.BookingRepository;
import brama.pressing_api.booking.service.BookingService;
import brama.pressing_api.config.PricingProperties;
import brama.pressing_api.exception.BusinessException;
import brama.pressing_api.exception.EntityNotFoundException;
import brama.pressing_api.exception.ErrorCode;
import brama.pressing_api.promotion.service.PromotionService;
import brama.pressing_api.utils.SecurityUtils;
import brama.pressing_api.vehicle.domain.model.Vehicle;
import brama.pressing_api.vehicle.domain.model.VehicleStatus;
import brama.pressing_api.vehicle.repo.VehicleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final VehicleRepository vehicleRepository;
    private final PromotionService promotionService;
    private final PricingProperties pricingProperties;

    @Override
    public BookingResponse create(final CreateBookingRequest request) {
        String userId = SecurityUtils.getCurrentUserId()
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        validateDateRange(request.getStartDate(), request.getEndDate());

        Vehicle vehicle = vehicleRepository.findById(request.getVehicleId())
                .orElseThrow(() -> new EntityNotFoundException("Vehicle not found"));
        if (vehicle.getStatus() == VehicleStatus.INACTIVE || vehicle.getStatus() == VehicleStatus.MAINTENANCE) {
            throw new BusinessException(ErrorCode.VEHICLE_NOT_AVAILABLE);
        }

        ensureAvailability(vehicle.getId(), request.getStartDate(), request.getEndDate());

        BookingPricing pricing = calculatePricing(vehicle, request);

        Booking booking = Booking.builder()
                .userId(userId)
                .vehicleId(vehicle.getId())
                .pickupLocationId(request.getPickupLocationId())
                .dropoffLocationId(request.getDropoffLocationId())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .status(BookingStatus.PENDING)
                .paymentStatus(BookingPaymentStatus.UNPAID)
                .extras(BookingMapper.toExtras(request.getExtras()))
                .driver(BookingMapper.toDriverDetails(request.getDriver()))
                .notes(request.getNotes())
                .promoCode(request.getPromoCode())
                .pricing(pricing)
                .build();

        return BookingMapper.toResponse(bookingRepository.save(booking));
    }

    @Override
    public List<BookingResponse> listMyBookings() {
        String userId = SecurityUtils.getCurrentUserId()
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        return bookingRepository.findByUserId(userId)
                .stream()
                .map(BookingMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public BookingResponse getMyBooking(final String bookingId) {
        String userId = SecurityUtils.getCurrentUserId()
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        Booking booking = bookingRepository.findByIdAndUserId(bookingId, userId)
                .orElseThrow(() -> new EntityNotFoundException("Booking not found"));
        return BookingMapper.toResponse(booking);
    }

    @Override
    public BookingResponse cancelMyBooking(final String bookingId) {
        String userId = SecurityUtils.getCurrentUserId()
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        Booking booking = bookingRepository.findByIdAndUserId(bookingId, userId)
                .orElseThrow(() -> new EntityNotFoundException("Booking not found"));
        if (booking.getStatus() == BookingStatus.CANCELED || booking.getStatus() == BookingStatus.COMPLETED) {
            throw new BusinessException(ErrorCode.BOOKING_STATUS_NOT_ALLOWED);
        }
        booking.setStatus(BookingStatus.CANCELED);
        return BookingMapper.toResponse(bookingRepository.save(booking));
    }

    @Override
    public Page<BookingResponse> listAdmin(final Pageable pageable) {
        return bookingRepository.findAll(pageable).map(BookingMapper::toResponse);
    }

    @Override
    public BookingResponse updateStatus(final String bookingId, final BookingStatus status) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new EntityNotFoundException("Booking not found"));
        booking.setStatus(status);
        return BookingMapper.toResponse(bookingRepository.save(booking));
    }

    private void validateDateRange(final LocalDate startDate, final LocalDate endDate) {
        if (startDate == null || endDate == null || !startDate.isBefore(endDate)) {
            throw new BusinessException(ErrorCode.INVALID_DATE_RANGE);
        }
    }

    private void ensureAvailability(final String vehicleId, final LocalDate startDate, final LocalDate endDate) {
        List<BookingStatus> activeStatuses = List.of(BookingStatus.CONFIRMED, BookingStatus.ACTIVE);
        Set<String> reservedVehicleIds = bookingRepository
                .findByOverlappingDates(startDate, endDate, activeStatuses)
                .stream()
                .map(Booking::getVehicleId)
                .collect(Collectors.toSet());

        if (reservedVehicleIds.contains(vehicleId)) {
            throw new BusinessException(ErrorCode.VEHICLE_NOT_AVAILABLE);
        }
    }

    private BookingPricing calculatePricing(final Vehicle vehicle, final CreateBookingRequest request) {
        int days = Math.toIntExact(ChronoUnit.DAYS.between(request.getStartDate(), request.getEndDate()));
        BigDecimal dailyRate = vehicle.getDailyRate();
        BigDecimal base = dailyRate.multiply(BigDecimal.valueOf(days));

        BigDecimal extrasTotal = BookingMapper.toExtras(request.getExtras())
                .stream()
                .map(extra -> {
                    BigDecimal price = extra.getPricePerDay() != null ? extra.getPricePerDay() : BigDecimal.ZERO;
                    int quantity = extra.getQuantity() != null ? extra.getQuantity() : 1;
                    return price.multiply(BigDecimal.valueOf(quantity)).multiply(BigDecimal.valueOf(days));
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal subtotal = base.add(extrasTotal);
        BigDecimal discount = promotionService.calculateDiscount(request.getPromoCode(), subtotal);
        BigDecimal fees = pricingProperties.getFees() != null ? pricingProperties.getFees() : BigDecimal.ZERO;
        BigDecimal taxRate = pricingProperties.getTaxRate() != null ? pricingProperties.getTaxRate() : BigDecimal.ZERO;
        BigDecimal taxable = subtotal.subtract(discount).max(BigDecimal.ZERO);
        BigDecimal taxes = taxable.multiply(taxRate);
        BigDecimal total = taxable.add(taxes).add(fees);

        return BookingPricing.builder()
                .dailyRate(dailyRate)
                .days(days)
                .extrasTotal(scale(extrasTotal))
                .subtotal(scale(subtotal))
                .discount(scale(discount))
                .taxes(scale(taxes))
                .fees(scale(fees))
                .total(scale(total))
                .deposit(vehicle.getDeposit() != null ? scale(vehicle.getDeposit()) : BigDecimal.ZERO)
                .currency(pricingProperties.getCurrency())
                .build();
    }

    private BigDecimal scale(final BigDecimal value) {
        return value.setScale(2, RoundingMode.HALF_UP);
    }
}
