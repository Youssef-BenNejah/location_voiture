package brama.pressing_api.vehicle.domain.model;

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

@Document(collection = "vehicles")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class Vehicle extends BaseDocument {
    @Field("make")
    private String make;

    @Field("model")
    private String model;

    @Field("year")
    private Integer year;

    @Field("trim")
    private String trim;

    @Field("category")
    @Indexed
    private VehicleCategory category;

    @Field("transmission")
    private TransmissionType transmission;

    @Field("fuel_type")
    private FuelType fuelType;

    @Field("seats")
    private Integer seats;

    @Field("doors")
    private Integer doors;

    @Field("luggage_capacity")
    private Integer luggageCapacity;

    @Field("color")
    private String color;

    @Field("license_plate")
    private String licensePlate;

    @Field("vin")
    private String vin;

    @Field("location_id")
    @Indexed
    private String locationId;

    @Field("daily_rate")
    @Indexed
    private BigDecimal dailyRate;

    @Field("weekly_rate")
    private BigDecimal weeklyRate;

    @Field("monthly_rate")
    private BigDecimal monthlyRate;

    @Field("deposit")
    private BigDecimal deposit;

    @Field("mileage_limit_per_day")
    private Integer mileageLimitPerDay;

    @Field("status")
    @Indexed
    private VehicleStatus status;

    @Field("description")
    private String description;

    @Field("features")
    private List<String> features;

    @Field("images")
    private List<String> images;

    @Field("rating_avg")
    private Double ratingAverage;

    @Field("rating_count")
    private Integer ratingCount;
}
