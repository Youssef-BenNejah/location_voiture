package brama.pressing_api.payment.stripe;

import brama.pressing_api.exception.BusinessException;
import brama.pressing_api.exception.ErrorCode;
import brama.pressing_api.payment.dto.request.ConfirmStripePaymentRequest;
import brama.pressing_api.payment.dto.request.CreateStripePaymentIntentRequest;
import brama.pressing_api.payment.dto.response.StripePaymentIntentResponse;
import brama.pressing_api.utils.SecurityUtils;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Charge;
import com.stripe.model.Event;
import com.stripe.model.PaymentIntent;
import com.stripe.model.Refund;
import com.stripe.net.Webhook;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/payments/stripe")
@RequiredArgsConstructor
@Tag(name = "Stripe Payments", description = "Stripe payment intents and webhooks")
public class StripePaymentController {
    private final StripePaymentService stripePaymentService;
    private final StripeProperties stripeProperties;

    @PostMapping("/intent")
    public ResponseEntity<StripePaymentIntentResponse> createIntent(
            @Valid @RequestBody CreateStripePaymentIntentRequest request) {
        String userId = SecurityUtils.getCurrentUserId()
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(stripePaymentService.createPaymentIntent(request, userId));
    }

    @PostMapping("/confirm")
    public ResponseEntity<StripePaymentIntentResponse> confirmPayment(
            @Valid @RequestBody ConfirmStripePaymentRequest request) {
        String userId = SecurityUtils.getCurrentUserId()
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        return ResponseEntity.ok(stripePaymentService.confirmPayment(request, userId));
    }

    @PostMapping("/webhook")
    public ResponseEntity<String> handleWebhook(@RequestBody String payload,
                                                 @RequestHeader("Stripe-Signature") String signature) {
        try {
            Event event = Webhook.constructEvent(payload, signature, stripeProperties.getWebhookSecret());
            if (event.getDataObjectDeserializer() != null) {
                Object stripeObject = event.getDataObjectDeserializer().getObject().orElse(null);
                if (stripeObject instanceof PaymentIntent intent) {
                    stripePaymentService.handleWebhookEvent(event.getType(), intent);
                } else if (stripeObject instanceof Charge charge) {
                    stripePaymentService.handleWebhookEventByIntentId(event.getType(), charge.getPaymentIntent());
                } else if (stripeObject instanceof Refund refund) {
                    String paymentIntentId = refund.getPaymentIntent();
                    if (paymentIntentId == null && refund.getCharge() != null) {
                        try {
                            Charge charge = Charge.retrieve(refund.getCharge());
                            paymentIntentId = charge.getPaymentIntent();
                        } catch (Exception ignored) {
                            paymentIntentId = null;
                        }
                    }
                    stripePaymentService.handleWebhookEventByIntentId(event.getType(), paymentIntentId);
                }
            }
            return ResponseEntity.ok("");
        } catch (SignatureVerificationException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid signature");
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Webhook error");
        }
    }
}
