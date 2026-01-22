package brama.pressing_api.location.domain.model;

import brama.pressing_api.common.BaseDocument;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Map;

@Document(collection = "locations")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class Location extends BaseDocument {
    @Field("name")
    @Indexed
    private String name;

    @Field("code")
    @Indexed(unique = true)
    private String code;

    @Field("address_line1")
    private String addressLine1;

    @Field("address_line2")
    private String addressLine2;

    @Field("city")
    private String city;

    @Field("state")
    private String state;

    @Field("postal_code")
    private String postalCode;

    @Field("country")
    private String country;

    @Field("latitude")
    private Double latitude;

    @Field("longitude")
    private Double longitude;

    @Field("phone")
    private String phone;

    @Field("email")
    private String email;

    @Field("timezone")
    private String timezone;

    @Field("is_active")
    private Boolean active;

    @Field("pickup_supported")
    private Boolean pickupSupported;

    @Field("dropoff_supported")
    private Boolean dropoffSupported;

    @Field("opening_hours")
    private Map<String, String> openingHours;
}
