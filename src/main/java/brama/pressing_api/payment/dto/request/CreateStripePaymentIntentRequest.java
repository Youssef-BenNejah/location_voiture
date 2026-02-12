package brama.pressing_api.payment.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateStripePaymentIntentRequest {
    private String bookingId;

    private String excursionBookingId;
}
