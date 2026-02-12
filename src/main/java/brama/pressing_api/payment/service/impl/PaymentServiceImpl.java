package brama.pressing_api.payment.service.impl;

import brama.pressing_api.booking.domain.model.Booking;
import brama.pressing_api.booking.domain.model.BookingPaymentEntry;
import brama.pressing_api.booking.domain.model.BookingPaymentStatus;
import brama.pressing_api.booking.domain.model.BookingStatus;
import brama.pressing_api.booking.repo.BookingRepository;
import brama.pressing_api.config.PricingProperties;
import brama.pressing_api.excursion.domain.model.Excursion;
import brama.pressing_api.excursion.repo.ExcursionRepository;
import brama.pressing_api.excursionbooking.domain.model.ExcursionBooking;
import brama.pressing_api.excursionbooking.domain.model.ExcursionBookingStatus;
import brama.pressing_api.excursionbooking.repo.ExcursionBookingRepository;
import brama.pressing_api.exception.BusinessException;
import brama.pressing_api.exception.EntityNotFoundException;
import brama.pressing_api.exception.ErrorCode;
import brama.pressing_api.payment.PaymentMapper;
import brama.pressing_api.payment.domain.model.Payment;
import brama.pressing_api.payment.domain.model.PaymentStatus;
import brama.pressing_api.payment.dto.request.CreatePaymentRequest;
import brama.pressing_api.payment.dto.response.PaymentResponse;
import brama.pressing_api.payment.repo.PaymentRepository;
import brama.pressing_api.payment.service.PaymentService;
import brama.pressing_api.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {
    private final PaymentRepository paymentRepository;
    private final BookingRepository bookingRepository;
    private final ExcursionBookingRepository excursionBookingRepository;
    private final ExcursionRepository excursionRepository;
    private final PricingProperties pricingProperties;

    @Override
    public PaymentResponse create(final CreatePaymentRequest request) {
        String userId = SecurityUtils.getCurrentUserId()
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        if (request == null) {
            throw new BusinessException(ErrorCode.BOOKING_INVALID_REQUEST);
        }

        String bookingId = normalizeId(request.getBookingId());
        String excursionBookingId = normalizeId(request.getExcursionBookingId());

        if (bookingId == null && excursionBookingId == null) {
            throw new BusinessException(ErrorCode.BOOKING_INVALID_REQUEST);
        }
        if (bookingId != null && excursionBookingId != null) {
            throw new BusinessException(ErrorCode.BOOKING_INVALID_REQUEST);
        }

        if (bookingId != null) {
            Booking booking = bookingRepository.findById(bookingId)
                    .orElseThrow(() -> new EntityNotFoundException("Booking not found"));

            if (!booking.getUserId().equals(userId)) {
                throw new BusinessException(ErrorCode.BOOKING_NOT_FOUND);
            }
            if (booking.getPaymentStatus() == BookingPaymentStatus.PAID) {
                throw new BusinessException(ErrorCode.PAYMENT_ALREADY_PROCESSED);
            }

            Payment payment = Payment.builder()
                    .bookingId(booking.getId())
                    .userId(userId)
                    .amount(booking.getPricing().getTotal())
                    .currency(booking.getPricing().getCurrency())
                    .provider(request.getProvider())
                    .method(request.getMethod())
                    .status(PaymentStatus.PENDING)
                    .build();

            return PaymentMapper.toResponse(paymentRepository.save(payment));
        }

        ExcursionBooking booking = excursionBookingRepository.findById(excursionBookingId)
                .orElseThrow(() -> new EntityNotFoundException("Excursion booking not found"));
        if (!belongsToUser(booking, userId)) {
            throw new EntityNotFoundException("Excursion booking not found");
        }
        if (booking.getStatus() == ExcursionBookingStatus.CANCELLED) {
            throw new BusinessException(ErrorCode.EXCURSION_BOOKING_STATUS_NOT_ALLOWED);
        }
        if (booking.getStatus() == ExcursionBookingStatus.CONFIRMED
                || booking.getStatus() == ExcursionBookingStatus.COMPLETED) {
            throw new BusinessException(ErrorCode.PAYMENT_ALREADY_PROCESSED);
        }

        BigDecimal total = booking.getTotalPrice() != null ? booking.getTotalPrice() : BigDecimal.ZERO;

        String currency = pricingProperties != null && pricingProperties.getCurrency() != null
                ? pricingProperties.getCurrency()
                : "USD";

        Payment payment = Payment.builder()
                .excursionBookingId(booking.getId())
                .userId(userId)
                .amount(total)
                .currency(currency)
                .provider(request.getProvider())
                .method(request.getMethod())
                .status(PaymentStatus.PENDING)
                .build();

        return PaymentMapper.toResponse(paymentRepository.save(payment));
    }

    @Override
    public List<PaymentResponse> listMyPayments() {
        String userId = SecurityUtils.getCurrentUserId()
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        return paymentRepository.findByUserId(userId)
                .stream()
                .map(PaymentMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public PaymentResponse getMyPayment(final String paymentId) {
        String userId = SecurityUtils.getCurrentUserId()
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        Payment payment = paymentRepository.findByIdAndUserId(paymentId, userId)
                .orElseThrow(() -> new EntityNotFoundException("Payment not found"));
        return PaymentMapper.toResponse(payment);
    }

    @Override
    public Page<PaymentResponse> listAdmin(final Pageable pageable) {
        return paymentRepository.findAll(pageable).map(PaymentMapper::toResponse);
    }

    @Override
    public PaymentResponse updateStatus(final String paymentId, final PaymentStatus status) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new EntityNotFoundException("Payment not found"));
        payment.setStatus(status);
        if (status == PaymentStatus.PAID) {
            payment.setPaidAt(LocalDateTime.now());
        }
        Payment saved = paymentRepository.save(payment);

        if (saved.getBookingId() != null && !saved.getBookingId().isBlank()) {
            Booking booking = bookingRepository.findById(saved.getBookingId())
                    .orElseThrow(() -> new EntityNotFoundException("Booking not found"));
            if (status == PaymentStatus.PAID) {
                updateBookingPayment(booking, saved);
                if (booking.getStatus() == null || booking.getStatus() == BookingStatus.PENDING) {
                    booking.setStatus(BookingStatus.CONFIRMED);
                }
            } else if (status == PaymentStatus.REFUNDED) {
                booking.setPaymentStatus(BookingPaymentStatus.REFUNDED);
            }
            bookingRepository.save(booking);
        } else if (saved.getExcursionBookingId() != null && !saved.getExcursionBookingId().isBlank()) {
            ExcursionBooking booking = excursionBookingRepository.findById(saved.getExcursionBookingId())
                    .orElseThrow(() -> new EntityNotFoundException("Excursion booking not found"));
            if (status == PaymentStatus.PAID) {
                if (booking.getStatus() == ExcursionBookingStatus.PENDING) {
                    booking.setStatus(ExcursionBookingStatus.CONFIRMED);
                }
            } else if (status == PaymentStatus.REFUNDED) {
                releaseExcursionSeatsIfNeeded(booking);
                booking.setStatus(ExcursionBookingStatus.CANCELLED);
            }
            excursionBookingRepository.save(booking);
        }

        return PaymentMapper.toResponse(saved);
    }

    private void updateBookingPayment(final Booking booking, final Payment payment) {
        if (booking == null || payment == null) {
            return;
        }
        if (booking.getPaymentHistory() == null) {
            booking.setPaymentHistory(new ArrayList<>());
        }
        booking.getPaymentHistory().add(BookingPaymentEntry.builder()
                .amount(payment.getAmount())
                .date(payment.getPaidAt() != null ? payment.getPaidAt() : LocalDateTime.now())
                .method(payment.getMethod() != null ? payment.getMethod().name() : null)
                .note("Payment")
                .build());
        if (booking.getPaidAmount() == null) {
            booking.setPaidAmount(payment.getAmount());
        } else {
            booking.setPaidAmount(booking.getPaidAmount().add(payment.getAmount()));
        }
        if (booking.getPricing() != null && booking.getPricing().getTotal() != null) {
            if (booking.getPaidAmount().compareTo(booking.getPricing().getTotal()) >= 0) {
                booking.setPaymentStatus(BookingPaymentStatus.PAID);
            } else {
                booking.setPaymentStatus(BookingPaymentStatus.PARTIAL);
            }
        } else {
            booking.setPaymentStatus(BookingPaymentStatus.PAID);
        }
    }

    private boolean belongsToUser(final ExcursionBooking booking, final String userId) {
        if (booking == null) {
            return false;
        }
        if (booking.getUserId() != null) {
            return booking.getUserId().equals(userId);
        }
        Optional<String> email = SecurityUtils.getCurrentUserEmail();
        return email.map(value -> value.equalsIgnoreCase(booking.getCustomerEmail())).orElse(false);
    }

    private void releaseExcursionSeatsIfNeeded(final ExcursionBooking booking) {
        if (booking == null) {
            return;
        }
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

    private String normalizeId(final String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
