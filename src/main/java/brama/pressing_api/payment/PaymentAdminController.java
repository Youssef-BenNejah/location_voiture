package brama.pressing_api.payment;

import brama.pressing_api.payment.dto.request.UpdatePaymentStatusRequest;
import brama.pressing_api.payment.dto.response.PaymentResponse;
import brama.pressing_api.payment.service.PaymentService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Admin endpoints to manage payments.
 */
@RestController
@RequestMapping("/api/v1/admin/payments")
@RequiredArgsConstructor
@Tag(name = "Payments - Admin", description = "Admin payment management")
@PreAuthorize("hasRole('ADMIN')")
public class PaymentAdminController {
    private final PaymentService paymentService;

    /**
     * Lists payments with pagination.
     */
    @GetMapping
    public Page<PaymentResponse> listPayments(Pageable pageable) {
        return paymentService.listAdmin(pageable);
    }

    /**
     * Updates payment status.
     */
    @PatchMapping("/{id}/status")
    public PaymentResponse updatePaymentStatus(@PathVariable String id,
                                               @Valid @RequestBody UpdatePaymentStatusRequest request) {
        return paymentService.updateStatus(id, request.getStatus());
    }
}
