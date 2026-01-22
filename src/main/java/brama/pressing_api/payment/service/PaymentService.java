package brama.pressing_api.payment.service;

import brama.pressing_api.payment.domain.model.PaymentStatus;
import brama.pressing_api.payment.dto.request.CreatePaymentRequest;
import brama.pressing_api.payment.dto.response.PaymentResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PaymentService {
    PaymentResponse create(CreatePaymentRequest request);

    List<PaymentResponse> listMyPayments();

    PaymentResponse getMyPayment(String paymentId);

    Page<PaymentResponse> listAdmin(Pageable pageable);

    PaymentResponse updateStatus(String paymentId, PaymentStatus status);
}
