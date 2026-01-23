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
public class CircuitBookingAdminStatsResponse {
    private long pendingCount;
    private long confirmedCount;
    private BigDecimal revenue;
}
