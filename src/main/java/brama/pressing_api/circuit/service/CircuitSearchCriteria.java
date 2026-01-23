package brama.pressing_api.circuit.service;

import brama.pressing_api.circuit.domain.CircuitStatus;
import brama.pressing_api.circuit.domain.CircuitVehicleType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CircuitSearchCriteria {
    private String query;
    private String originCity;
    private String destinationCity;
    private CircuitVehicleType vehicleType;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    private CircuitStatus status;
}
