package brama.pressing_api.payment.dto.request;

import brama.pressing_api.payment.domain.model.PaymentMethod;
import brama.pressing_api.payment.domain.model.PaymentProvider;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreatePaymentRequest {
    @NotBlank
    private String bookingId;

    @NotNull
    private PaymentProvider provider;

    @NotNull
    private PaymentMethod method;
}
