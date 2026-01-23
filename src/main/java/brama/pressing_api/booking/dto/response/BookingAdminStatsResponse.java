package brama.pressing_api.booking.dto.response;

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
public class BookingAdminStatsResponse {
    private long pendingCount;
    private long approvedCount;
    private long activeCount;
    private long completedCount;
    private long unpaidCount;
    private long partialCount;
    private long paidCount;
    private BigDecimal collectedAmount;
    private BigDecimal pendingAmount;
}
