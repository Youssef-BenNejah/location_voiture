package brama.pressing_api.booking.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateBookingRequest {
    @NotBlank
    private String vehicleId;

    @NotBlank
    private String pickupLocationId;

    @NotBlank
    private String dropoffLocationId;

    @NotNull
    private LocalDate startDate;

    @NotNull
    private LocalDate endDate;

    @Valid
    private List<BookingExtraRequest> extras;

    @Valid
    private DriverDetailsRequest driver;

    private String notes;

    private String promoCode;
}
