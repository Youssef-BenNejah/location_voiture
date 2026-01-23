package brama.pressing_api.circuit.domain;

import brama.pressing_api.common.BaseDocument;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.math.BigDecimal;
import java.util.List;

@Document(collection = "circuits")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class Circuit extends BaseDocument {
    @Field("title")
    @Indexed
    private String title;

    @Field("description")
    private String description;

    @Field("origin_city")
    @Indexed
    private String originCity;

    @Field("destination_city")
    @Indexed
    private String destinationCity;

    @Field("distance")
    private Integer distance;

    @Field("estimated_duration")
    private String estimatedDuration;

    @Field("price")
    @Indexed
    private BigDecimal price;

    @Field("currency")
    private String currency;

    @Field("vehicle_type")
    @Indexed
    private CircuitVehicleType vehicleType;

    @Field("max_passengers")
    private Integer maxPassengers;

    @Field("status")
    @Indexed
    private CircuitStatus status;

    @Field("images")
    private List<String> images;
}
