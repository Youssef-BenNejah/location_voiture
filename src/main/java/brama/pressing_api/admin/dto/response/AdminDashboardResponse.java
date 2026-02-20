package brama.pressing_api.admin.dto.response;

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
public class AdminDashboardResponse {
    private LocalDateTime generatedAt;
    private int windowDays;
    private Snapshot snapshot;
    private BookingMetrics booking;
    private FleetMetrics fleet;
    private CustomerMetrics customer;
    private PaymentMetrics payment;
    private ExperienceMetrics experience;
    private DemandMetrics demand;
    private List<String> alerts;

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Snapshot {
        private BigDecimal totalRevenuePaid;
        private BigDecimal revenueInWindow;
        private BigDecimal revenuePreviousWindow;
        private double revenueGrowthPercent;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BookingMetrics {
        private long totalBookings;
        private long bookingsInWindow;
        private long activeBookings;
        private long overdueActiveBookings;
        private double completionRatePercent;
        private double cancellationRatePercent;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FleetMetrics {
        private long totalVehicles;
        private long availableVehicles;
        private long reservedVehicles;
        private long rentedVehicles;
        private long maintenanceVehicles;
        private double utilizationRatePercent;
        private double availabilityRatePercent;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CustomerMetrics {
        private long totalCustomers;
        private long newCustomersInWindow;
        private double repeatCustomerRatePercent;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PaymentMetrics {
        private long totalPayments;
        private long paidPayments;
        private long failedPayments;
        private double paymentSuccessRatePercent;
        private BigDecimal outstandingReceivables;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ExperienceMetrics {
        private long totalReviews;
        private long pendingReviews;
        private long approvedReviews;
        private double averageApprovedRating;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DemandMetrics {
        private long vehicleBookingsInWindow;
        private long excursionBookingsInWindow;
        private long circuitBookingsInWindow;
        private String dominantChannel;
    }
}
