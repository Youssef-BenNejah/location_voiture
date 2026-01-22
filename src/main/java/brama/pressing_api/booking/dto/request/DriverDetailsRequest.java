package brama.pressing_api.booking.dto.request;

import jakarta.validation.constraints.NotBlank;
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
public class DriverDetailsRequest {
    @NotBlank
    private String fullName;

    private String licenseNumber;
    private String licenseCountry;
    private LocalDate dateOfBirth;
    private String phone;
    private String email;
}
