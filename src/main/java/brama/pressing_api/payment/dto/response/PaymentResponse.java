package brama.pressing_api.payment.dto.response;

import brama.pressing_api.payment.domain.model.PaymentMethod;
import brama.pressing_api.payment.domain.model.PaymentProvider;
import brama.pressing_api.payment.domain.model.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponse {
    private String id;
    private String bookingId;
    private String excursionBookingId;
    private String userId;
    private BigDecimal amount;
    private String currency;
    private PaymentProvider provider;
    private PaymentMethod method;
    private PaymentStatus status;
    private String transactionId;
    private LocalDateTime paidAt;
    private LocalDateTime createdDate;
    private LocalDateTime lastModifiedDate;
}
