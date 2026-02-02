package brama.pressing_api.booking.service.impl;

import brama.pressing_api.booking.BookingMapper;
import brama.pressing_api.booking.domain.model.Booking;
import brama.pressing_api.booking.domain.model.BookingCreatedBy;
import brama.pressing_api.booking.domain.model.BookingPaymentStatus;
import brama.pressing_api.booking.domain.model.BookingPricing;
import brama.pressing_api.booking.domain.model.BookingStatus;
import brama.pressing_api.booking.domain.model.BookingPaymentEntry;
import brama.pressing_api.booking.dto.request.AdminCreateBookingRequest;
import brama.pressing_api.booking.dto.request.CreateBookingRequest;
import brama.pressing_api.booking.dto.request.RecordBookingPaymentRequest;
import brama.pressing_api.booking.dto.response.BookingAdminStatsResponse;
import brama.pressing_api.booking.dto.response.BookingResponse;
import brama.pressing_api.booking.repo.BookingRepository;
import brama.pressing_api.booking.service.BookingService;
import brama.pressing_api.booking.service.BookingSearchCriteria;
import brama.pressing_api.config.PricingProperties;
import brama.pressing_api.exception.BusinessException;
import brama.pressing_api.exception.EntityNotFoundException;
import brama.pressing_api.exception.ErrorCode;
import brama.pressing_api.promotion.service.PromotionService;
import brama.pressing_api.utils.SecurityUtils;
import brama.pressing_api.vehicle.domain.model.Vehicle;
import brama.pressing_api.vehicle.domain.model.VehicleStatus;
import brama.pressing_api.vehicle.repo.VehicleRepository;
import brama.pressing_api.payment.domain.model.Payment;
import brama.pressing_api.payment.domain.model.PaymentProvider;
import brama.pressing_api.payment.domain.model.PaymentStatus;
import brama.pressing_api.payment.repo.PaymentRepository;
import brama.pressing_api.user.User;
import brama.pressing_api.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final VehicleRepository vehicleRepository;
    private final PromotionService promotionService;
    private final PricingProperties pricingProperties;
    private final PaymentRepository paymentRepository;
    private final UserRepository userRepository;

    @Override
    public BookingResponse create(final CreateBookingRequest request) {
        String userId = SecurityUtils.getCurrentUserId()
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        User customer = userRepository.findById(userId)
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
                .customerName(buildCustomerName(customer))
                .customerEmail(customer.getEmail())
                .customerPhone(customer.getPhoneNumber())
                .vehicleId(vehicle.getId())
                .vehicleName(resolveVehicleName(vehicle))
                .pickupLocationId(request.getPickupLocationId())
                .dropoffLocationId(request.getDropoffLocationId())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .status(BookingStatus.PENDING)
                .paymentStatus(BookingPaymentStatus.UNPAID)
                .bookingCreatedBy(BookingCreatedBy.CLIENT)
                .paidAmount(BigDecimal.ZERO)
                .paymentHistory(new ArrayList<>())
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
    public Page<BookingResponse> searchAdmin(final BookingSearchCriteria criteria, final Pageable pageable) {
        return bookingRepository.search(criteria, pageable).map(BookingMapper::toResponse);
    }

    @Override
    public BookingResponse createAdmin(final AdminCreateBookingRequest request, final String adminId) {
        if (request == null) {
            throw new BusinessException(ErrorCode.BOOKING_INVALID_REQUEST);
        }
        User customer = userRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        validateDateRange(request.getStartDate(), request.getEndDate());

        Vehicle vehicle = vehicleRepository.findById(request.getVehicleId())
                .orElseThrow(() -> new EntityNotFoundException("Vehicle not found"));
        if (vehicle.getStatus() == VehicleStatus.INACTIVE || vehicle.getStatus() == VehicleStatus.MAINTENANCE) {
            throw new BusinessException(ErrorCode.VEHICLE_NOT_AVAILABLE);
        }

        ensureAvailability(vehicle.getId(), request.getStartDate(), request.getEndDate());

        BookingPricing pricing = calculatePricing(vehicle, CreateBookingRequest.builder()
                .vehicleId(request.getVehicleId())
                .pickupLocationId(request.getPickupLocationId())
                .dropoffLocationId(request.getDropoffLocationId())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .notes(request.getNotes())
                .promoCode(request.getPromoCode())
                .build());

        Booking booking = Booking.builder()
                .userId(request.getCustomerId())
                .customerName(buildCustomerName(customer))
                .customerEmail(customer.getEmail())
                .customerPhone(customer.getPhoneNumber())
                .vehicleId(vehicle.getId())
                .vehicleName(resolveVehicleName(vehicle))
                .pickupLocationId(request.getPickupLocationId())
                .dropoffLocationId(request.getDropoffLocationId())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .status(BookingStatus.PENDING)
                .paymentStatus(BookingPaymentStatus.UNPAID)
                .bookingCreatedBy(BookingCreatedBy.ADMIN)
                .paidAmount(BigDecimal.ZERO)
                .paymentHistory(new ArrayList<>())
                .notes(request.getNotes())
                .promoCode(request.getPromoCode())
                .pricing(pricing)
                .build();

        Booking saved = bookingRepository.save(booking);

        if (request.getInitialPayment() != null && request.getInitialPayment().compareTo(BigDecimal.ZERO) > 0) {
            recordPayment(saved.getId(),
                    buildAdminPaymentRequest(request.getInitialPayment(), request.getPaymentMethod(), "Initial payment"),
                    adminId);
            return BookingMapper.toResponse(bookingRepository.findById(saved.getId())
                    .orElse(saved));
        }

        return BookingMapper.toResponse(saved);
    }

    @Override
    public BookingResponse recordPayment(final String bookingId,
                                         final RecordBookingPaymentRequest request,
                                         final String adminId) {
        if (request == null || request.getAmount() == null || request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException(ErrorCode.PAYMENT_AMOUNT_INVALID);
        }
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new EntityNotFoundException("Booking not found"));

        BigDecimal total = booking.getPricing() != null ? booking.getPricing().getTotal() : BigDecimal.ZERO;
        BigDecimal paidAmount = booking.getPaidAmount() != null ? booking.getPaidAmount() : BigDecimal.ZERO;
        BigDecimal remaining = total.subtract(paidAmount);
        if (remaining.compareTo(BigDecimal.ZERO) > 0 && request.getAmount().compareTo(remaining) > 0) {
            throw new BusinessException(ErrorCode.PAYMENT_AMOUNT_INVALID);
        }
        BigDecimal newPaid = paidAmount.add(request.getAmount());
        booking.setPaidAmount(newPaid);

        List<BookingPaymentEntry> history = booking.getPaymentHistory();
        if (history == null) {
            history = new ArrayList<>();
        }
        history.add(BookingPaymentEntry.builder()
                .amount(request.getAmount())
                .date(LocalDateTime.now())
                .method(request.getMethod().name())
                .note(request.getNote())
                .build());
        booking.setPaymentHistory(history);

        booking.setPaymentStatus(resolvePaymentStatus(newPaid, total));
        bookingRepository.save(booking);

        Payment payment = Payment.builder()
                .bookingId(booking.getId())
                .userId(booking.getUserId())
                .amount(request.getAmount())
                .currency(booking.getPricing() != null ? booking.getPricing().getCurrency() : pricingProperties.getCurrency())
                .provider(PaymentProvider.MANUAL)
                .method(request.getMethod())
                .status(PaymentStatus.PAID)
                .paidAt(LocalDateTime.now())
                .build();
        paymentRepository.save(payment);

        return BookingMapper.toResponse(booking);
    }

    @Override
    public BookingAdminStatsResponse getAdminStats() {
        List<Booking> bookings = bookingRepository.findAll();
        BigDecimal collected = BigDecimal.ZERO;
        BigDecimal pendingAmount = BigDecimal.ZERO;
        long unpaidCount = 0;
        long partialCount = 0;
        long paidCount = 0;
        for (Booking booking : bookings) {
            BigDecimal total = booking.getPricing() != null ? booking.getPricing().getTotal() : BigDecimal.ZERO;
            BigDecimal paid = booking.getPaidAmount() != null ? booking.getPaidAmount() : BigDecimal.ZERO;
            collected = collected.add(paid);
            BigDecimal remaining = total.subtract(paid);
            if (remaining.compareTo(BigDecimal.ZERO) > 0) {
                pendingAmount = pendingAmount.add(remaining);
            }
            if (booking.getPaymentStatus() == BookingPaymentStatus.UNPAID) {
                unpaidCount++;
            } else if (booking.getPaymentStatus() == BookingPaymentStatus.PARTIAL) {
                partialCount++;
            } else if (booking.getPaymentStatus() == BookingPaymentStatus.PAID) {
                paidCount++;
            }
        }

        return BookingAdminStatsResponse.builder()
                .pendingCount(bookingRepository.countByStatus(BookingStatus.PENDING))
                .approvedCount(bookingRepository.countByStatus(BookingStatus.CONFIRMED))
                .activeCount(bookingRepository.countByStatus(BookingStatus.ACTIVE))
                .completedCount(bookingRepository.countByStatus(BookingStatus.COMPLETED))
                .unpaidCount(unpaidCount)
                .partialCount(partialCount)
                .paidCount(paidCount)
                .collectedAmount(collected)
                .pendingAmount(pendingAmount)
                .build();
    }

    @Override
    public String exportCsv(final BookingSearchCriteria criteria) {
        List<Booking> bookings = bookingRepository.search(criteria, Pageable.unpaged()).getContent();
        StringBuilder builder = new StringBuilder();
        builder.append("id,customerName,customerEmail,customerPhone,vehicleName,startDate,endDate,status,paymentStatus,total,paidAmount,createdBy,createdDate\n");
        for (Booking booking : bookings) {
            builder.append(nullSafe(booking.getId())).append(',')
                    .append(csv(booking.getCustomerName())).append(',')
                    .append(csv(booking.getCustomerEmail())).append(',')
                    .append(csv(booking.getCustomerPhone())).append(',')
                    .append(csv(booking.getVehicleName())).append(',')
                    .append(nullSafe(booking.getStartDate())).append(',')
                    .append(nullSafe(booking.getEndDate())).append(',')
                    .append(booking.getStatus() != null ? booking.getStatus().name() : "").append(',')
                    .append(booking.getPaymentStatus() != null ? booking.getPaymentStatus().name() : "").append(',')
                    .append(nullSafe(booking.getPricing() != null ? booking.getPricing().getTotal() : null)).append(',')
                    .append(nullSafe(booking.getPaidAmount())).append(',')
                    .append(booking.getCreatedBy() != null ? booking.getBookingCreatedBy().name() : "").append(',')
                    .append(nullSafe(booking.getCreatedDate()))
                    .append('\n');
        }
        return builder.toString();
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

    private BookingPaymentStatus resolvePaymentStatus(final BigDecimal paid, final BigDecimal total) {
        if (paid == null || paid.compareTo(BigDecimal.ZERO) <= 0) {
            return BookingPaymentStatus.UNPAID;
        }
        if (total != null && paid.compareTo(total) >= 0) {
            return BookingPaymentStatus.PAID;
        }
        return BookingPaymentStatus.PARTIAL;
    }

    private RecordBookingPaymentRequest buildAdminPaymentRequest(final BigDecimal amount,
                                                                 final brama.pressing_api.payment.domain.model.PaymentMethod method,
                                                                 final String note) {
        RecordBookingPaymentRequest request = new RecordBookingPaymentRequest();
        request.setAmount(amount);
        request.setMethod(method != null ? method : brama.pressing_api.payment.domain.model.PaymentMethod.CASH);
        request.setNote(note);
        return request;
    }

    private String buildCustomerName(final User user) {
        if (user == null) {
            return null;
        }
        return List.of(user.getFirstName(), user.getLastName()).stream()
                .filter(Objects::nonNull)
                .filter(value -> !value.isBlank())
                .collect(Collectors.joining(" "))
                .trim();
    }

    private String resolveVehicleName(Vehicle vehicle) {
        List<String> nameParts = new ArrayList<>();

        if (vehicle.getMake() != null && !vehicle.getMake().isBlank()) {
            nameParts.add(vehicle.getMake());
        }

        if (vehicle.getModel() != null && !vehicle.getModel().isBlank()) {
            nameParts.add(vehicle.getModel());
        }

        if (vehicle.getYear() != null) {
            nameParts.add(vehicle.getYear().toString());
        }

        if (nameParts.isEmpty()) {
            return "Vehicle " + vehicle.getId();
        }

        return String.join(" ", nameParts);
    }
    private String csv(final String value) {
        if (value == null) {
            return "";
        }
        String escaped = value.replace("\"", "\"\"");
        return "\"" + escaped + "\"";
    }

    private String nullSafe(final Object value) {
        return value == null ? "" : value.toString();
    }
}