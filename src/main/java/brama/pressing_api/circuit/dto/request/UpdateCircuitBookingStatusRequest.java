package brama.pressing_api.circuit.dto.request;

import brama.pressing_api.circuit.domain.CircuitBookingStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateCircuitBookingStatusRequest {
    @NotNull
    private CircuitBookingStatus status;
}
