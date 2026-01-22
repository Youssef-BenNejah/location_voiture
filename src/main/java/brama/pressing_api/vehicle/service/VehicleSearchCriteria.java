package brama.pressing_api.vehicle.service;

import brama.pressing_api.vehicle.domain.model.FuelType;
import brama.pressing_api.vehicle.domain.model.TransmissionType;
import brama.pressing_api.vehicle.domain.model.VehicleCategory;
import brama.pressing_api.vehicle.domain.model.VehicleStatus;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

@Getter
@Builder
public class VehicleSearchCriteria {
    private String locationId;
    private LocalDate startDate;
    private LocalDate endDate;
    private VehicleCategory category;
    private TransmissionType transmission;
    private FuelType fuelType;
    private Integer minSeats;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    private Set<VehicleStatus> statuses;
    private Set<String> excludeVehicleIds;
}
