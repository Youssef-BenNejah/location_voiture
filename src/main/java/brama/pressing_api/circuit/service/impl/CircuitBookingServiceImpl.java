package brama.pressing_api.circuit.service.impl;

import brama.pressing_api.circuit.CircuitMapper;
import brama.pressing_api.circuit.domain.Circuit;
import brama.pressing_api.circuit.domain.CircuitBooking;
import brama.pressing_api.circuit.domain.CircuitBookingStatus;
import brama.pressing_api.circuit.domain.CircuitStatus;
import brama.pressing_api.circuit.dto.request.CreateCircuitBookingRequest;
import brama.pressing_api.circuit.dto.request.UpdateCircuitBookingStatusRequest;
import brama.pressing_api.circuit.dto.response.CircuitBookingAdminStatsResponse;
import brama.pressing_api.circuit.dto.response.CircuitBookingResponse;
import brama.pressing_api.circuit.repo.CircuitBookingRepository;
import brama.pressing_api.circuit.repo.CircuitRepository;
import brama.pressing_api.circuit.service.CircuitBookingSearchCriteria;
import brama.pressing_api.circuit.service.CircuitBookingService;
import brama.pressing_api.exception.BusinessException;
import brama.pressing_api.exception.ErrorCode;
import brama.pressing_api.notification.domain.NotificationImportance;
import brama.pressing_api.notification.dto.NotificationRequest;
import brama.pressing_api.notification.service.NotificationService;
import brama.pressing_api.user.User;
import brama.pressing_api.user.UserRepository;
import brama.pressing_api.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CircuitBookingServiceImpl implements CircuitBookingService {
    private final CircuitRepository circuitRepository;
    private final CircuitBookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    @Override
    public CircuitBookingResponse createPublic(final String circuitId,
                                               final CreateCircuitBookingRequest request,
                                               final Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        User userFound = userRepository.findById(user.getId())
                .orElseThrow(() -> new BusinessException(ErrorCode.CIRCUIT_NOT_FOUND));
        Circuit circuit = circuitRepository.findById(circuitId)
                .orElseThrow(() -> new BusinessException(ErrorCode.CIRCUIT_NOT_FOUND));

        if (circuit.getStatus() != CircuitStatus.ACTIVE) {
            throw new BusinessException(ErrorCode.CIRCUIT_INACTIVE);
        }

        if (request.getNumberOfPassengers() != null && circuit.getMaxPassengers() != null
                && request.getNumberOfPassengers() > circuit.getMaxPassengers()) {
            throw new BusinessException(ErrorCode.CIRCUIT_PASSENGERS_LIMIT);
        }

        if (request.getSelectedDate() != null && request.getSelectedDate().isBefore(LocalDate.now())) {
            throw new BusinessException(ErrorCode.CIRCUIT_DATE_INVALID);
        }

        BigDecimal price = circuit.getPrice() != null ? circuit.getPrice() : BigDecimal.ZERO;
        BigDecimal total = price.multiply(BigDecimal.valueOf(request.getNumberOfPassengers()));

        // 🔥 Use userId if authenticated, otherwise null (for guest bookings)
        CircuitBooking booking = CircuitBooking.builder()
                .circuitId(circuit.getId())
                .userId(userFound.getId()) // This will be the authenticated user ID
                .circuitTitle(circuit.getTitle())
                .customerName(request.getCustomerName())
                .customerEmail(request.getCustomerEmail())
                .customerPhone(request.getCustomerPhone())
                .selectedDate(request.getSelectedDate())
                .selectedTime(request.getSelectedTime())
                .numberOfPassengers(request.getNumberOfPassengers())
                .totalPrice(total)
                .pickupAddress(request.getPickupAddress())
                .dropoffAddress(request.getDropoffAddress())
                .notes(request.getNotes())
                .status(CircuitBookingStatus.PENDING)
                .bookedAt(LocalDateTime.now())
                .build();

        CircuitBooking savedBooking = bookingRepository.save(booking);

        // 🔥 Debug log
        System.out.println("✅ Booking created with ID: " + savedBooking.getId());
        System.out.println("👤 User ID: " + savedBooking.getUserId());
        System.out.println("📧 Email: " + savedBooking.getCustomerEmail());

        notificationService.notifyAdmins(NotificationRequest.builder()
                .type("CIRCUIT_BOOKING_CREATED")
                .title("New circuit booking")
                .body("A client created circuit booking " + savedBooking.getId())
                .importance(NotificationImportance.HIGH)
                .data(java.util.Map.of("bookingId", savedBooking.getId(), "circuitId", savedBooking.getCircuitId()))
                .build());
        return CircuitMapper.toBookingResponse(savedBooking);
    }

    @Override
    public Page<CircuitBookingResponse> listAdmin(final CircuitBookingSearchCriteria criteria,
                                                  final Pageable pageable) {
        return bookingRepository.search(criteria, pageable).map(CircuitMapper::toBookingResponse);
    }

    @Override
    public CircuitBookingResponse updateStatus(final String bookingId,
                                               final UpdateCircuitBookingStatusRequest request) {
        CircuitBooking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new BusinessException(ErrorCode.CIRCUIT_BOOKING_NOT_FOUND));
        if (!isAllowedTransition(booking.getStatus(), request.getStatus())) {
            throw new BusinessException(ErrorCode.CIRCUIT_BOOKING_STATUS_NOT_ALLOWED);
        }
        booking.setStatus(request.getStatus());
        CircuitBooking saved = bookingRepository.save(booking);
        if (saved.getUserId() != null) {
            notificationService.notifyUser(saved.getUserId(), NotificationRequest.builder()
                    .type("CIRCUIT_BOOKING_STATUS_UPDATED")
                    .title("Circuit booking updated")
                    .body("Your circuit booking " + saved.getId() + " is now " + saved.getStatus())
                    .importance(NotificationImportance.HIGH)
                    .data(java.util.Map.of("bookingId", saved.getId(), "status", String.valueOf(saved.getStatus())))
                    .build());
        }
        return CircuitMapper.toBookingResponse(saved);
    }

    @Override
    public CircuitBookingResponse getAdmin(final String bookingId) {
        CircuitBooking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new BusinessException(ErrorCode.CIRCUIT_BOOKING_NOT_FOUND));
        return CircuitMapper.toBookingResponse(booking);
    }

    @Override
    public Page<CircuitBookingResponse> listMyBookings(final String userId, final Pageable pageable) {
        List<CircuitBooking> bookings = new ArrayList<>();
        bookings.addAll(bookingRepository.findByUserId(userId, pageable).getContent());
        Optional<String> email = SecurityUtils.getCurrentUserEmail();
        if (email.isPresent()) {
            bookings.addAll(bookingRepository.findByUserIdIsNullAndCustomerEmailIgnoreCase(email.get()));
        }
        List<CircuitBookingResponse> responses = uniqueBookings(bookings).stream()
                .map(CircuitMapper::toBookingResponse)
                .toList();
        return new org.springframework.data.domain.PageImpl<>(responses, pageable, responses.size());
    }

    @Override
    public CircuitBookingResponse getMyBooking(final String bookingId, final String userId) {
        CircuitBooking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new BusinessException(ErrorCode.CIRCUIT_BOOKING_NOT_FOUND));
        if (!belongsToUser(booking, userId)) {
            throw new BusinessException(ErrorCode.CIRCUIT_ACCESS_DENIED);
        }
        return CircuitMapper.toBookingResponse(booking);
    }

    @Override
    public CircuitBookingResponse cancelMyBooking(final String bookingId, final String userId) {
        CircuitBooking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new BusinessException(ErrorCode.CIRCUIT_BOOKING_NOT_FOUND));
        if (!belongsToUser(booking, userId)) {
            throw new BusinessException(ErrorCode.CIRCUIT_ACCESS_DENIED);
        }
        if (booking.getStatus() != CircuitBookingStatus.PENDING) {
            throw new BusinessException(ErrorCode.CIRCUIT_BOOKING_STATUS_NOT_ALLOWED);
        }
        booking.setStatus(CircuitBookingStatus.CANCELLED);
        CircuitBooking saved = bookingRepository.save(booking);
        notificationService.notifyAdmins(NotificationRequest.builder()
                .type("CIRCUIT_BOOKING_CANCELLED")
                .title("Circuit booking cancelled")
                .body("Client cancelled circuit booking " + saved.getId())
                .importance(NotificationImportance.NORMAL)
                .data(java.util.Map.of("bookingId", saved.getId(), "circuitId", saved.getCircuitId()))
                .build());
        return CircuitMapper.toBookingResponse(saved);
    }

    @Override
    public CircuitBookingResponse getMyTicket(final String bookingId, final String userId) {
        return getMyBooking(bookingId, userId);
    }

    @Override
    public CircuitBookingAdminStatsResponse getAdminStats() {
        List<CircuitBooking> bookings = bookingRepository.findAll();
        long pending = bookings.stream()
                .filter(booking -> booking.getStatus() == CircuitBookingStatus.PENDING)
                .count();
        long confirmed = bookings.stream()
                .filter(booking -> booking.getStatus() == CircuitBookingStatus.CONFIRMED)
                .count();
        BigDecimal revenue = BigDecimal.ZERO;
        for (CircuitBooking booking : bookings) {
            if (booking.getTotalPrice() != null) {
                revenue = revenue.add(booking.getTotalPrice());
            }
        }
        return CircuitBookingAdminStatsResponse.builder()
                .pendingCount(pending)
                .confirmedCount(confirmed)
                .revenue(revenue)
                .build();
    }

    private boolean isAllowedTransition(final CircuitBookingStatus current, final CircuitBookingStatus next) {
        if (current == null || next == null) {
            return false;
        }
        if (current == CircuitBookingStatus.PENDING) {
            return next == CircuitBookingStatus.CONFIRMED || next == CircuitBookingStatus.CANCELLED;
        }
        if (current == CircuitBookingStatus.CONFIRMED) {
            return next == CircuitBookingStatus.COMPLETED;
        }
        return false;
    }

    private boolean belongsToUser(final CircuitBooking booking, final String userId) {
        if (booking.getUserId() != null) {
            return booking.getUserId().equals(userId);
        }
        Optional<String> email = SecurityUtils.getCurrentUserEmail();
        return email.map(value -> value.equalsIgnoreCase(booking.getCustomerEmail())).orElse(false);
    }

    private List<CircuitBooking> uniqueBookings(final List<CircuitBooking> bookings) {
        Map<String, CircuitBooking> unique = new LinkedHashMap<>();
        for (CircuitBooking booking : bookings) {
            if (booking.getId() != null) {
                unique.put(booking.getId(), booking);
            }
        }
        return new ArrayList<>(unique.values());
    }
}
