package brama.pressing_api.payment.domain.model;

import brama.pressing_api.common.BaseDocument;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Document(collection = "payments")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class Payment extends BaseDocument {
    @Field("booking_id")
    @Indexed
    private String bookingId;

    @Field("user_id")
    @Indexed
    private String userId;

    @Field("amount")
    private BigDecimal amount;

    @Field("currency")
    private String currency;

    @Field("provider")
    private PaymentProvider provider;

    @Field("method")
    private PaymentMethod method;

    @Field("status")
    @Indexed
    private PaymentStatus status;

    @Field("transaction_id")
    private String transactionId;

    @Field("paid_at")
    private LocalDateTime paidAt;
}
