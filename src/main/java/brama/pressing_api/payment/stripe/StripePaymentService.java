package brama.pressing_api.payment.stripe;

import brama.pressing_api.booking.domain.model.Booking;
import brama.pressing_api.booking.domain.model.BookingPaymentStatus;
import brama.pressing_api.booking.repo.BookingRepository;
import brama.pressing_api.exception.BusinessException;
import brama.pressing_api.exception.EntityNotFoundException;
import brama.pressing_api.exception.ErrorCode;
import brama.pressing_api.payment.domain.model.Payment;
import brama.pressing_api.payment.domain.model.PaymentMethod;
import brama.pressing_api.payment.domain.model.PaymentProvider;
import brama.pressing_api.payment.domain.model.PaymentStatus;
import brama.pressing_api.payment.dto.request.ConfirmStripePaymentRequest;
import brama.pressing_api.payment.dto.request.CreateStripePaymentIntentRequest;
import brama.pressing_api.payment.dto.response.StripePaymentIntentResponse;
import brama.pressing_api.payment.repo.PaymentRepository;
import brama.pressing_api.payment.service.PaymentService;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
@RequiredArgsConstructor
public class StripePaymentService {
    private final BookingRepository bookingRepository;
    private final PaymentRepository paymentRepository;
    private final PaymentService paymentService;

    public StripePaymentIntentResponse createPaymentIntent(final CreateStripePaymentIntentRequest request,
                                                           final String userId) {
        if (request == null || request.getBookingId() == null) {
            throw new BusinessException(ErrorCode.BOOKING_INVALID_REQUEST);
        }
        Booking booking = bookingRepository.findById(request.getBookingId())
                .orElseThrow(() -> new EntityNotFoundException("Booking not found"));
        if (booking.getUserId() != null && !booking.getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.BOOKING_NOT_FOUND);
        }
        if (booking.getPaymentStatus() == BookingPaymentStatus.PAID) {
            throw new BusinessException(ErrorCode.PAYMENT_ALREADY_PROCESSED);
        }

        BigDecimal total = booking.getPricing() != null ? booking.getPricing().getTotal() : BigDecimal.ZERO;
        String currency = booking.getPricing() != null ? booking.getPricing().getCurrency() : "USD";

        Payment payment = Payment.builder()
                .bookingId(booking.getId())
                .userId(booking.getUserId())
                .amount(total)
                .currency(currency)
                .provider(PaymentProvider.STRIPE)
                .method(PaymentMethod.CARD)
                .status(PaymentStatus.PENDING)
                .build();
        Payment saved = paymentRepository.save(payment);

        long amountInMinor = total.multiply(BigDecimal.valueOf(100))
                .setScale(0, RoundingMode.HALF_UP)
                .longValue();

        PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                .setAmount(amountInMinor)
                .setCurrency(currency.toLowerCase())
                .putMetadata("bookingId", booking.getId())
                .putMetadata("paymentId", saved.getId())
                .setAutomaticPaymentMethods(
                        PaymentIntentCreateParams.AutomaticPaymentMethods.builder()
                                .setEnabled(true)
                                .build())
                .build();

        try {
            PaymentIntent intent = PaymentIntent.create(params);
            saved.setTransactionId(intent.getId());
            paymentRepository.save(saved);
            return StripePaymentIntentResponse.builder()
                    .paymentId(saved.getId())
                    .bookingId(booking.getId())
                    .paymentIntentId(intent.getId())
                    .clientSecret(intent.getClientSecret())
                    .amount(total)
                    .currency(currency)
                    .build();
        } catch (StripeException ex) {
            throw new BusinessException(ErrorCode.INTERNAL_EXCEPTION, ex.getMessage());
        }
    }

    public StripePaymentIntentResponse confirmPayment(final ConfirmStripePaymentRequest request,
                                                      final String userId) {
        if (request == null || request.getPaymentIntentId() == null) {
            throw new BusinessException(ErrorCode.BOOKING_INVALID_REQUEST);
        }
        Payment payment = paymentRepository.findByTransactionId(request.getPaymentIntentId())
                .orElseThrow(() -> new EntityNotFoundException("Payment not found"));
        if (payment.getUserId() != null && !payment.getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.BOOKING_NOT_FOUND);
        }

        try {
            PaymentIntent intent = PaymentIntent.retrieve(request.getPaymentIntentId());
            PaymentStatus status = mapStatus(intent.getStatus());
            paymentService.updateStatus(payment.getId(), status);

            return StripePaymentIntentResponse.builder()
                    .paymentId(payment.getId())
                    .bookingId(payment.getBookingId())
                    .paymentIntentId(intent.getId())
                    .clientSecret(intent.getClientSecret())
                    .amount(payment.getAmount())
                    .currency(payment.getCurrency())
                    .build();
        } catch (StripeException ex) {
            throw new BusinessException(ErrorCode.INTERNAL_EXCEPTION, ex.getMessage());
        }
    }

    public void handleWebhookEvent(final String eventType, final PaymentIntent intent) {
        if (intent == null || intent.getId() == null) {
            return;
        }
        handleWebhookEventByIntentId(eventType, intent.getId());
    }

    public void handleWebhookEventByIntentId(final String eventType, final String paymentIntentId) {
        if (paymentIntentId == null || paymentIntentId.isBlank()) {
            return;
        }
        Payment payment = paymentRepository.findByTransactionId(paymentIntentId)
                .orElse(null);
        if (payment == null) {
            return;
        }
        PaymentStatus status = mapStatusForEvent(eventType);
        if (status == null) {
            return;
        }
        paymentService.updateStatus(payment.getId(), status);
    }

    private PaymentStatus mapStatus(final String stripeStatus) {
        if ("succeeded".equals(stripeStatus)) {
            return PaymentStatus.PAID;
        }
        if ("canceled".equals(stripeStatus)) {
            return PaymentStatus.FAILED;
        }
        if ("requires_payment_method".equals(stripeStatus) || "requires_action".equals(stripeStatus)) {
            return PaymentStatus.FAILED;
        }
        return PaymentStatus.PENDING;
    }

    private PaymentStatus mapStatusForEvent(final String eventType) {
        if ("payment_intent.succeeded".equals(eventType)) {
            return PaymentStatus.PAID;
        }
        if ("payment_intent.payment_failed".equals(eventType) || "payment_intent.canceled".equals(eventType)) {
            return PaymentStatus.FAILED;
        }
        if ("charge.refunded".equals(eventType) || "refund.succeeded".equals(eventType)) {
            return PaymentStatus.REFUNDED;
        }
        return null;
    }
}
