package brama.pressing_api.vehicle.dto.response;

import brama.pressing_api.vehicle.domain.model.FuelType;
import brama.pressing_api.vehicle.domain.model.TransmissionType;
import brama.pressing_api.vehicle.domain.model.VehicleCategory;
import brama.pressing_api.vehicle.domain.model.VehicleStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VehicleResponse {
    private String id;
    private String make;
    private String model;
    private Integer year;
    private String trim;
    private VehicleCategory category;
    private TransmissionType transmission;
    private FuelType fuelType;
    private Integer seats;
    private Integer doors;
    private Integer luggageCapacity;
    private String color;
    private String licensePlate;
    private String vin;
    private String locationId;
    private BigDecimal dailyRate;
    private BigDecimal weeklyRate;
    private BigDecimal monthlyRate;
    private BigDecimal deposit;
    private Integer mileageLimitPerDay;
    private VehicleStatus status;
    private String description;
    private List<String> features;
    private List<String> images;
    private Double ratingAverage;
    private Integer ratingCount;
    private LocalDateTime createdDate;
    private LocalDateTime lastModifiedDate;
}
