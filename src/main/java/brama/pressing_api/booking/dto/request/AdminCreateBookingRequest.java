package brama.pressing_api.booking.dto.request;

import brama.pressing_api.payment.domain.model.PaymentMethod;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminCreateBookingRequest {
    @NotBlank
    private String customerId;

    @NotBlank
    private String vehicleId;


    private String pickupLocationId;


    private String dropoffLocationId;

    @NotNull
    private LocalDate startDate;

    @NotNull
    private LocalDate endDate;

    private BigDecimal initialPayment;

    private PaymentMethod paymentMethod;

    private String notes;

    private String promoCode;
}
