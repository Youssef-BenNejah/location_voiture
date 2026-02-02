package brama.pressing_api.seed.location_seed.domain;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "locations")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LocationSeed {
    @Id
    private String id;

    private String name;

    private String address;

    private String city;

    private String state;

    private String country;

    private String postalCode;

    private boolean active;

    private LocalDateTime createdDate;

    private LocalDateTime lastModifiedDate;
}
