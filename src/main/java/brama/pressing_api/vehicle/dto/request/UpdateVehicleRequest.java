package brama.pressing_api.vehicle.dto.request;

import brama.pressing_api.vehicle.domain.model.FuelType;
import brama.pressing_api.vehicle.domain.model.TransmissionType;
import brama.pressing_api.vehicle.domain.model.VehicleCategory;
import brama.pressing_api.vehicle.domain.model.VehicleStatus;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateVehicleRequest {
    private String make;
    private String model;

    @Positive
    private Integer year;

    private String trim;
    private VehicleCategory category;
    private TransmissionType transmission;
    private FuelType fuelType;

    @Positive
    private Integer seats;

    @Positive
    private Integer doors;

    @PositiveOrZero
    private Integer luggageCapacity;

    private String color;
    private String licensePlate;
    private String vin;
    private String locationId;

    @Positive
    private BigDecimal dailyRate;

    @Positive
    private BigDecimal weeklyRate;

    @Positive
    private BigDecimal monthlyRate;

    @PositiveOrZero
    private BigDecimal deposit;

    @PositiveOrZero
    private Integer mileageLimitPerDay;

    private VehicleStatus status;
    private String description;
    private List<String> features;
    private List<String> images;
}
