package brama.pressing_api.circuit.dto.response;

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
public class CircuitAdminStatsResponse {
    private long totalCircuits;
    private long activeCircuits;
    private long totalBookings;
    private BigDecimal revenue;
}
