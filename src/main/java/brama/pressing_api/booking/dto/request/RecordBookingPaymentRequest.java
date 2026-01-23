package brama.pressing_api.booking.dto.request;

import brama.pressing_api.payment.domain.model.PaymentMethod;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class RecordBookingPaymentRequest {
    @NotNull
    private BigDecimal amount;

    @NotNull
    private PaymentMethod method;

    private String note;
}
