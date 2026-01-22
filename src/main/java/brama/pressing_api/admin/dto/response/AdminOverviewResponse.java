package brama.pressing_api.admin.dto.response;

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
public class AdminOverviewResponse {
    private long totalVehicles;
    private long availableVehicles;
    private long totalBookings;
    private long activeBookings;
    private long totalCustomers;
    private long totalLocations;
    private long totalPayments;
    private long totalReviews;
    private BigDecimal totalRevenue;
}
