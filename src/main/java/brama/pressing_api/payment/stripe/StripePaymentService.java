package brama.pressing_api.payment.stripe;

import brama.pressing_api.booking.domain.model.Booking;
import brama.pressing_api.booking.domain.model.BookingPaymentStatus;
import brama.pressing_api.booking.repo.BookingRepository;
import brama.pressing_api.config.PricingProperties;
import brama.pressing_api.excursionbooking.domain.model.ExcursionBooking;
import brama.pressing_api.excursionbooking.domain.model.ExcursionBookingStatus;
import brama.pressing_api.excursionbooking.repo.ExcursionBookingRepository;
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
import brama.pressing_api.utils.SecurityUtils;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class StripePaymentService {
    private final BookingRepository bookingRepository;
    private final ExcursionBookingRepository excursionBookingRepository;
    private final PaymentRepository paymentRepository;
    private final PaymentService paymentService;
    private final PricingProperties pricingProperties;

    public StripePaymentIntentResponse createPaymentIntent(final CreateStripePaymentIntentRequest request,
                                                           final String userId) {
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
            return createBookingIntent(bookingId, userId);
        }
        return createExcursionIntent(excursionBookingId, userId);
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
                    .excursionBookingId(payment.getExcursionBookingId())
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

        System.out.println("Webhook received: " + eventType + " for intent: " + paymentIntentId);

        Payment payment = paymentRepository.findByTransactionId(paymentIntentId)
                .orElse(null);
        if (payment == null) {
            System.out.println("Payment not found for intent: " + paymentIntentId);
            return;
        }

        PaymentStatus status = mapStatusForEvent(eventType);
        if (status == null) {
            System.out.println("Unknown event type: " + eventType);
            return;
        }

        System.out.println("Updating payment " + payment.getId() + " to status: " + status);
        paymentService.updateStatus(payment.getId(), status);
    }

    private StripePaymentIntentResponse createBookingIntent(final String bookingId, final String userId) {
        Booking booking = bookingRepository.findById(bookingId)
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
                .putMetadata("userId", userId)
                .setAutomaticPaymentMethods(
                        PaymentIntentCreateParams.AutomaticPaymentMethods.builder()
                                .setEnabled(true)
                                .build())
                .build();

        try {
            PaymentIntent intent = PaymentIntent.create(params);
            saved.setTransactionId(intent.getId());
            paymentRepository.save(saved);

            if ("succeeded".equals(intent.getStatus())) {
                paymentService.updateStatus(saved.getId(), PaymentStatus.PAID);
            }

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

    private StripePaymentIntentResponse createExcursionIntent(final String excursionBookingId, final String userId) {
        System.out.println("🔍 Looking for excursion booking with ID: " + excursionBookingId);

        ExcursionBooking booking = excursionBookingRepository.findById(excursionBookingId)
                .orElseThrow(() -> {
                    System.out.println("❌ Excursion booking NOT FOUND: " + excursionBookingId);
                    return new EntityNotFoundException("Excursion booking not found");
                });

        System.out.println("✅ Found booking with ID: " + booking.getId());

        if (!belongsToUser(booking, userId)) {
            System.out.println("⚠️ Booking found but doesn't belong to user: " + userId);
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
                .putMetadata("excursionBookingId", booking.getId())
                .putMetadata("paymentId", saved.getId())
                .putMetadata("userId", userId)
                .setAutomaticPaymentMethods(
                        PaymentIntentCreateParams.AutomaticPaymentMethods.builder()
                                .setEnabled(true)
                                .build())
                .build();

        try {
            PaymentIntent intent = PaymentIntent.create(params);
            saved.setTransactionId(intent.getId());
            paymentRepository.save(saved);

            if ("succeeded".equals(intent.getStatus())) {
                paymentService.updateStatus(saved.getId(), PaymentStatus.PAID);
            }

            return StripePaymentIntentResponse.builder()
                    .paymentId(saved.getId())
                    .excursionBookingId(booking.getId())
                    .paymentIntentId(intent.getId())
                    .clientSecret(intent.getClientSecret())
                    .amount(total)
                    .currency(currency)
                    .build();
        } catch (StripeException ex) {
            throw new BusinessException(ErrorCode.INTERNAL_EXCEPTION, ex.getMessage());
        }
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

    private boolean belongsToUser(final ExcursionBooking booking, final String userId) {
        if (booking == null) {
            return false;
        }

        // If booking has a userId, it must match
        if (booking.getUserId() != null) {
            return booking.getUserId().equals(userId);
        }

        // For guest bookings, check if the current user's email matches
        if (booking.getCustomerEmail() != null) {
            Optional<String> currentEmail = SecurityUtils.getCurrentUserEmail();
            if (currentEmail.isPresent()) {
                boolean emailMatches = currentEmail.get().equalsIgnoreCase(booking.getCustomerEmail());
                System.out.println("📧 Email comparison: " + currentEmail.get() + " vs " + booking.getCustomerEmail() + " = " + emailMatches);
                return emailMatches;
            }
        }

        // If no way to verify ownership, deny access
        return false;
    }

    private String normalizeId(final String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
