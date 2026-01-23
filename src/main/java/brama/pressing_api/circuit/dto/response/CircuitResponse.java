package brama.pressing_api.circuit.dto.response;

import brama.pressing_api.circuit.domain.CircuitStatus;
import brama.pressing_api.circuit.domain.CircuitVehicleType;
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
public class CircuitResponse {
    private String id;
    private String title;
    private String description;
    private String originCity;
    private String destinationCity;
    private Integer distance;
    private String estimatedDuration;
    private BigDecimal price;
    private String currency;
    private CircuitVehicleType vehicleType;
    private Integer maxPassengers;
    private CircuitStatus status;
    private List<String> images;
    private Long bookingsCount;
    private LocalDateTime createdDate;
    private LocalDateTime lastModifiedDate;
}
