package brama.pressing_api.payment;

import brama.pressing_api.payment.dto.request.CreatePaymentRequest;
import brama.pressing_api.payment.dto.response.PaymentResponse;
import brama.pressing_api.payment.service.PaymentService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Customer payment endpoints (create and view payments).
 */
@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
@Tag(name = "Payments", description = "Customer payments")
public class PaymentController {
    private final PaymentService paymentService;

    /**
     * Creates a payment for a booking or an excursion booking.
     */
    @PostMapping
    public ResponseEntity<PaymentResponse> createPayment(@Valid @RequestBody CreatePaymentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(paymentService.create(request));
    }

    /**
     * Lists payments for the authenticated user.
     */
    @GetMapping
    public List<PaymentResponse> listMyPayments() {
        return paymentService.listMyPayments();
    }

    /**
     * Returns a single payment owned by the authenticated user.
     */
    @GetMapping("/{id}")
    public PaymentResponse getMyPayment(@PathVariable String id) {
        return paymentService.getMyPayment(id);
    }
}
