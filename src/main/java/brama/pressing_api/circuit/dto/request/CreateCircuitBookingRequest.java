package brama.pressing_api.circuit.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateCircuitBookingRequest {
    @NotBlank
    private String customerName;

    @Email
    @NotBlank
    private String customerEmail;

    @NotBlank
    private String customerPhone;

    @NotNull
    private LocalDate selectedDate;

    @NotBlank
    private String selectedTime;

    @NotNull
    @Min(1)
    private Integer numberOfPassengers;

    private String pickupAddress;

    private String dropoffAddress;

    private String notes;
}
