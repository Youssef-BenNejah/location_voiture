package brama.pressing_api.excursionbooking.service.impl;

import brama.pressing_api.email.EmailService;
import brama.pressing_api.excursion.domain.model.Excursion;
import brama.pressing_api.excursion.repo.ExcursionRepository;
import brama.pressing_api.excursionbooking.ExcursionBookingMapper;
import brama.pressing_api.excursionbooking.domain.model.ExcursionBooking;
import brama.pressing_api.excursionbooking.domain.model.ExcursionBookingStatus;
import brama.pressing_api.excursionbooking.dto.request.CreateExcursionBookingRequest;
import brama.pressing_api.excursionbooking.dto.response.ExcursionBookingResponse;
import brama.pressing_api.excursionbooking.dto.response.ExcursionBookingTicketResponse;
import brama.pressing_api.excursionbooking.repo.ExcursionBookingRepository;
import brama.pressing_api.excursionbooking.service.ExcursionBookingSearchCriteria;
import brama.pressing_api.excursionbooking.service.ExcursionBookingService;
import brama.pressing_api.exception.BusinessException;
import brama.pressing_api.exception.EntityNotFoundException;
import brama.pressing_api.exception.ErrorCode;
import brama.pressing_api.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ExcursionBookingServiceImpl implements ExcursionBookingService {
    private static final int MAX_PUBLIC_SEATS = 10;

    private final ExcursionBookingRepository bookingRepository;
    private final ExcursionRepository excursionRepository;
    private final EmailService emailService;

    @Override
    @CacheEvict(cacheNames = {"excursions", "excursion"}, allEntries = true)
    public ExcursionBookingResponse createPublic(final String excursionId,
                                                 final CreateExcursionBookingRequest request) {
        Excursion excursion = excursionRepository.findById(excursionId)
                .orElseThrow(() -> new EntityNotFoundException("Excursion not found"));
        if (!Boolean.TRUE.equals(excursion.getIsEnabled())) {
            throw new BusinessException(ErrorCode.EXCURSION_DISABLED);
        }
        validateDateAvailable(excursion, request);
        int remainingCapacity = remainingCapacity(excursion);
        int seats = request.getNumberOfSeats();
        if (seats > MAX_PUBLIC_SEATS) {
            throw new BusinessException(ErrorCode.EXCURSION_SEATS_LIMIT);
        }
        if (remainingCapacity <= 0 || seats > remainingCapacity) {
            throw new BusinessException(ErrorCode.EXCURSION_FULL);
        }

        BigDecimal price = excursion.getPricePerPerson() != null ? excursion.getPricePerPerson() : BigDecimal.ZERO;
        BigDecimal totalPrice = price.multiply(BigDecimal.valueOf(seats));

        ExcursionBooking booking = ExcursionBooking.builder()
                .excursionId(excursion.getId())
                .excursionTitle(excursion.getTitle())
                .userId(SecurityUtils.getCurrentUserId().orElse(null))
                .customerName(request.getCustomerName())
                .customerEmail(request.getCustomerEmail())
                .customerPhone(request.getCustomerPhone())
                .selectedDate(request.getSelectedDate())
                .numberOfSeats(seats)
                .totalPrice(totalPrice)
                .status(ExcursionBookingStatus.PENDING)
                .bookedAt(LocalDateTime.now())
                .build();

        excursion.setBookedSeats((excursion.getBookedSeats() != null ? excursion.getBookedSeats() : 0) + seats);
        excursionRepository.save(excursion);

        return ExcursionBookingMapper.toResponse(bookingRepository.save(booking));
    }

    @Override
    public List<ExcursionBookingResponse> listMyBookings() {
        String userId = SecurityUtils.getCurrentUserId()
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        List<ExcursionBooking> bookings = new ArrayList<>(bookingRepository.findByUserId(userId));
        Optional<String> email = SecurityUtils.getCurrentUserEmail();
        if (email.isPresent()) {
            bookings.addAll(bookingRepository.findByUserIdIsNullAndCustomerEmailIgnoreCase(email.get()));
        }
        return uniqueBookings(bookings)
                .stream()
                .map(ExcursionBookingMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public ExcursionBookingResponse getMyBooking(final String bookingId) {
        String userId = SecurityUtils.getCurrentUserId()
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        ExcursionBooking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new EntityNotFoundException("Excursion booking not found"));
        if (!belongsToUser(booking, userId)) {
            throw new EntityNotFoundException("Excursion booking not found");
        }
        return ExcursionBookingMapper.toResponse(booking);
    }

    @Override
    @CacheEvict(cacheNames = {"excursions", "excursion"}, allEntries = true)
    public ExcursionBookingResponse cancelMyBooking(final String bookingId) {
        String userId = SecurityUtils.getCurrentUserId()
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        ExcursionBooking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new EntityNotFoundException("Excursion booking not found"));
        if (!belongsToUser(booking, userId)) {
            throw new EntityNotFoundException("Excursion booking not found");
        }
        if (booking.getStatus() == ExcursionBookingStatus.CANCELLED
                || booking.getStatus() == ExcursionBookingStatus.COMPLETED) {
            throw new BusinessException(ErrorCode.EXCURSION_BOOKING_STATUS_NOT_ALLOWED);
        }
        releaseSeatsIfNeeded(booking);
        booking.setStatus(ExcursionBookingStatus.CANCELLED);
        return ExcursionBookingMapper.toResponse(bookingRepository.save(booking));
    }

    @Override
    public ExcursionBookingTicketResponse getMyTicket(final String bookingId) {
        String userId = SecurityUtils.getCurrentUserId()
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        ExcursionBooking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new EntityNotFoundException("Excursion booking not found"));
        if (!belongsToUser(booking, userId)) {
            throw new EntityNotFoundException("Excursion booking not found");
        }
        Excursion excursion = excursionRepository.findById(booking.getExcursionId())
                .orElseThrow(() -> new EntityNotFoundException("Excursion not found"));
        return ExcursionBookingMapper.toTicketResponse(booking, excursion);
    }

    @Override
    public Page<ExcursionBookingResponse> listAdmin(final ExcursionBookingSearchCriteria criteria,
                                                    final Pageable pageable) {
        return bookingRepository.search(criteria, pageable).map(ExcursionBookingMapper::toResponse);
    }

    @Override
    public ExcursionBookingResponse getAdminBooking(final String bookingId) {
        ExcursionBooking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new EntityNotFoundException("Excursion booking not found"));
        return ExcursionBookingMapper.toResponse(booking);
    }

    @Override
    @CacheEvict(cacheNames = {"excursions", "excursion"}, allEntries = true)
    public ExcursionBookingResponse updateStatus(final String bookingId, final ExcursionBookingStatus status) {
        ExcursionBooking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new EntityNotFoundException("Excursion booking not found"));
        validateTransition(booking.getStatus(), status);
        if (status == ExcursionBookingStatus.CANCELLED) {
            releaseSeatsIfNeeded(booking);
        }
        booking.setStatus(status);
        return ExcursionBookingMapper.toResponse(bookingRepository.save(booking));
    }

    @Override
    public String exportCsv(final ExcursionBookingSearchCriteria criteria) {
        List<ExcursionBooking> bookings = bookingRepository.search(criteria, Pageable.unpaged()).getContent();
        StringBuilder builder = new StringBuilder();
        builder.append("id,excursionId,excursionTitle,customerName,customerEmail,customerPhone,selectedDate,numberOfSeats,totalPrice,status,bookedAt\n");
        for (ExcursionBooking booking : bookings) {
            builder.append(nullSafe(booking.getId())).append(',')
                    .append(nullSafe(booking.getExcursionId())).append(',')
                    .append(csv(booking.getExcursionTitle())).append(',')
                    .append(csv(booking.getCustomerName())).append(',')
                    .append(csv(booking.getCustomerEmail())).append(',')
                    .append(csv(booking.getCustomerPhone())).append(',')
                    .append(nullSafe(booking.getSelectedDate())).append(',')
                    .append(nullSafe(booking.getNumberOfSeats())).append(',')
                    .append(nullSafe(booking.getTotalPrice())).append(',')
                    .append(booking.getStatus() != null ? booking.getStatus().name() : "")
                    .append(',')
                    .append(nullSafe(booking.getBookedAt()))
                    .append('\n');
        }
        return builder.toString();
    }

    @Override
    public void sendConfirmation(final String bookingId) {
        ExcursionBooking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new EntityNotFoundException("Excursion booking not found"));
        Excursion excursion = excursionRepository.findById(booking.getExcursionId())
                .orElseThrow(() -> new EntityNotFoundException("Excursion not found"));
        String subject = "Your excursion booking confirmation";
        String body = "Hello " + booking.getCustomerName() + ",\n\n"
                + "Your booking for " + booking.getExcursionTitle() + " is " + booking.getStatus() + ".\n"
                + "Date: " + booking.getSelectedDate() + "\n"
                + "Seats: " + booking.getNumberOfSeats() + "\n"
                + "Total: " + booking.getTotalPrice() + "\n"
                + "Start: " + excursion.getStartLocation() + "\n"
                + "End: " + excursion.getEndLocation() + "\n\n"
                + "Thank you.";
        try {
            emailService.sendPlainTextEmail(booking.getCustomerEmail(), subject, body);
        } catch (Exception ex) {
            log.warn("Failed to send confirmation for booking {}", bookingId, ex);
        }
    }

    private boolean belongsToUser(final ExcursionBooking booking, final String userId) {
        if (booking.getUserId() != null) {
            return Objects.equals(booking.getUserId(), userId);
        }
        Optional<String> email = SecurityUtils.getCurrentUserEmail();
        return email.map(value -> value.equalsIgnoreCase(booking.getCustomerEmail())).orElse(false);
    }

    private void validateDateAvailable(final Excursion excursion, final CreateExcursionBookingRequest request) {
        if (excursion.getAvailableDates() != null && !excursion.getAvailableDates().isEmpty()) {
            if (!excursion.getAvailableDates().contains(request.getSelectedDate())) {
                throw new BusinessException(ErrorCode.EXCURSION_DATE_NOT_AVAILABLE);
            }
        }
    }

    private int remainingCapacity(final Excursion excursion) {
        int total = excursion.getTotalCapacity() != null ? excursion.getTotalCapacity() : 0;
        int booked = excursion.getBookedSeats() != null ? excursion.getBookedSeats() : 0;
        return total - booked;
    }

    private void releaseSeatsIfNeeded(final ExcursionBooking booking) {
        if (booking.getStatus() != ExcursionBookingStatus.PENDING
                && booking.getStatus() != ExcursionBookingStatus.CONFIRMED) {
            return;
        }
        Excursion excursion = excursionRepository.findById(booking.getExcursionId())
                .orElseThrow(() -> new EntityNotFoundException("Excursion not found"));
        int bookedSeats = excursion.getBookedSeats() != null ? excursion.getBookedSeats() : 0;
        int seatsToRelease = booking.getNumberOfSeats() != null ? booking.getNumberOfSeats() : 0;
        int updated = Math.max(0, bookedSeats - seatsToRelease);
        excursion.setBookedSeats(updated);
        excursionRepository.save(excursion);
    }

    private void validateTransition(final ExcursionBookingStatus current, final ExcursionBookingStatus target) {
        if (current == null || target == null || current == target) {
            return;
        }
        switch (current) {
            case PENDING -> {
                if (target != ExcursionBookingStatus.CONFIRMED && target != ExcursionBookingStatus.CANCELLED) {
                    throw new BusinessException(ErrorCode.EXCURSION_BOOKING_STATUS_NOT_ALLOWED);
                }
            }
            case CONFIRMED -> {
                if (target != ExcursionBookingStatus.CANCELLED && target != ExcursionBookingStatus.COMPLETED) {
                    throw new BusinessException(ErrorCode.EXCURSION_BOOKING_STATUS_NOT_ALLOWED);
                }
            }
            case CANCELLED, COMPLETED -> throw new BusinessException(ErrorCode.EXCURSION_BOOKING_STATUS_NOT_ALLOWED);
        }
    }

    private List<ExcursionBooking> uniqueBookings(final List<ExcursionBooking> bookings) {
        Map<String, ExcursionBooking> unique = new LinkedHashMap<>();
        for (ExcursionBooking booking : bookings) {
            if (booking.getId() != null) {
                unique.put(booking.getId(), booking);
            }
        }
        return new ArrayList<>(unique.values());
    }

    private String nullSafe(final Object value) {
        return value == null ? "" : String.valueOf(value);
    }

    private String csv(final String value) {
        if (value == null) {
            return "";
        }
        String escaped = value.replace("\"", "\"\"");
        return '"' + escaped + '"';
    }
}
