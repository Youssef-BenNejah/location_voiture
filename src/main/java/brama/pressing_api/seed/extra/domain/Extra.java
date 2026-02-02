package brama.pressing_api.seed.extra.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Extra (add-on) entity for booking extras like GPS, child seat, etc.
 */
@Document(collection = "extras")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Extra {

    @Id
    private String id;

    private String name;

    private String description;

    private BigDecimal pricePerDay;

    private String category; // DRIVER, SAFETY, ELECTRONICS, INSURANCE, etc.

    private boolean active;

    private LocalDateTime createdDate;

    private LocalDateTime lastModifiedDate;
}