package brama.pressing_api.payment.service.impl;

import brama.pressing_api.booking.domain.model.Booking;
import brama.pressing_api.booking.domain.model.BookingPaymentStatus;
import brama.pressing_api.booking.domain.model.BookingStatus;
import brama.pressing_api.booking.repo.BookingRepository;
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

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {
    private final PaymentRepository paymentRepository;
    private final BookingRepository bookingRepository;

    @Override
    public PaymentResponse create(final CreatePaymentRequest request) {
        String userId = SecurityUtils.getCurrentUserId()
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        Booking booking = bookingRepository.findById(request.getBookingId())
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

        Booking booking = bookingRepository.findById(saved.getBookingId())
                .orElseThrow(() -> new EntityNotFoundException("Booking not found"));
        if (status == PaymentStatus.PAID) {
            booking.setPaymentStatus(BookingPaymentStatus.PAID);
            if (booking.getStatus() == null || booking.getStatus() == BookingStatus.PENDING) {
                booking.setStatus(BookingStatus.CONFIRMED);
            }
        } else if (status == PaymentStatus.REFUNDED) {
            booking.setPaymentStatus(BookingPaymentStatus.REFUNDED);
        }
        bookingRepository.save(booking);

        return PaymentMapper.toResponse(saved);
    }
}
