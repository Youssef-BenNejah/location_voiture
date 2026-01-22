package brama.pressing_api.payment;

import brama.pressing_api.payment.domain.model.Payment;
import brama.pressing_api.payment.dto.response.PaymentResponse;

public final class PaymentMapper {
    private PaymentMapper() {
    }

    public static PaymentResponse toResponse(final Payment payment) {
        return PaymentResponse.builder()
                .id(payment.getId())
                .bookingId(payment.getBookingId())
                .userId(payment.getUserId())
                .amount(payment.getAmount())
                .currency(payment.getCurrency())
                .provider(payment.getProvider())
                .method(payment.getMethod())
                .status(payment.getStatus())
                .transactionId(payment.getTransactionId())
                .paidAt(payment.getPaidAt())
                .createdDate(payment.getCreatedDate())
                .lastModifiedDate(payment.getLastModifiedDate())
                .build();
    }
}
