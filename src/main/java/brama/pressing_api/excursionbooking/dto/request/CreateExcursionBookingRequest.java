package brama.pressing_api.excursionbooking.dto.request;

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
public class CreateExcursionBookingRequest {
    @NotBlank
    private String customerName;

    @NotBlank
    @Email
    private String customerEmail;

    @NotBlank
    private String customerPhone;

    @NotNull
    private LocalDate selectedDate;

    @NotNull
    @Min(1)
    private Integer numberOfSeats;
}
