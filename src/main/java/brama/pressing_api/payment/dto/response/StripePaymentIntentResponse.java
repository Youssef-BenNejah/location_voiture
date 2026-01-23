package brama.pressing_api.payment.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StripePaymentIntentResponse {
    private String paymentId;
    private String bookingId;
    private String paymentIntentId;
    private String clientSecret;
    private BigDecimal amount;
    private String currency;
}
