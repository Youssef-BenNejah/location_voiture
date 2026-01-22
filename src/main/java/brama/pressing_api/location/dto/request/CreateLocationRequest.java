package brama.pressing_api.location.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateLocationRequest {
    @NotBlank
    private String name;

    @NotBlank
    private String code;

    @NotBlank
    private String addressLine1;

    private String addressLine2;

    @NotBlank
    private String city;

    private String state;

    private String postalCode;

    @NotBlank
    private String country;

    private Double latitude;
    private Double longitude;
    private String phone;
    private String email;
    private String timezone;
    private Boolean active;
    private Boolean pickupSupported;
    private Boolean dropoffSupported;
    private Map<String, String> openingHours;
}
