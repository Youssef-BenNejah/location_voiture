package brama.pressing_api.circuit.dto.request;

import brama.pressing_api.circuit.domain.CircuitStatus;
import brama.pressing_api.circuit.domain.CircuitVehicleType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
public class CircuitRequest {
    @NotBlank
    private String title;

    private String description;

    @NotBlank
    private String originCity;

    @NotBlank
    private String destinationCity;

    private Integer distance;

    private String estimatedDuration;

    @NotNull
    private BigDecimal price;

    @NotBlank
    private String currency;

    @NotNull
    private CircuitVehicleType vehicleType;

    @NotNull
    private Integer maxPassengers;

    @NotNull
    private CircuitStatus status;

    private List<String> images;
}
