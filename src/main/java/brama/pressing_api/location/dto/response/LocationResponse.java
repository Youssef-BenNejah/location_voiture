package brama.pressing_api.location.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Map;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LocationResponse {
    private String id;
    private String name;
    private String code;
    private String addressLine1;
    private String addressLine2;
    private String city;
    private String state;
    private String postalCode;
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
    private LocalDateTime createdDate;
    private LocalDateTime lastModifiedDate;
}
