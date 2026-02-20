package brama.pressing_api.admin.service.impl;

import brama.pressing_api.admin.dto.response.AdminDashboardResponse;
import brama.pressing_api.admin.dto.response.AdminOverviewResponse;
import brama.pressing_api.admin.service.AdminAnalyticsService;
import brama.pressing_api.booking.domain.model.BookingStatus;
import brama.pressing_api.booking.repo.BookingRepository;
import brama.pressing_api.location.repo.LocationRepository;
import brama.pressing_api.payment.domain.model.PaymentStatus;
import brama.pressing_api.payment.repo.PaymentRepository;
import brama.pressing_api.review.domain.model.ReviewStatus;
import brama.pressing_api.review.repo.ReviewRepository;
import brama.pressing_api.user.UserRepository;
import brama.pressing_api.vehicle.domain.model.VehicleStatus;
import brama.pressing_api.vehicle.repo.VehicleRepository;
import org.bson.Document;
import org.bson.types.Decimal128;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminAnalyticsServiceImpl implements AdminAnalyticsService {
    private static final int MIN_DASHBOARD_WINDOW_DAYS = 7;
    private static final int MAX_DASHBOARD_WINDOW_DAYS = 365;

    private final VehicleRepository vehicleRepository;
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final LocationRepository locationRepository;
    private final PaymentRepository paymentRepository;
    private final ReviewRepository reviewRepository;
    private final MongoTemplate mongoTemplate;

    @Override
    public AdminOverviewResponse getOverview() {
        long totalVehicles = vehicleRepository.count();
        long availableVehicles = vehicleRepository.countByStatus(VehicleStatus.AVAILABLE);
        long totalBookings = bookingRepository.count();
        long activeBookings = bookingRepository.countByStatus(BookingStatus.ACTIVE);
        long totalCustomers = userRepository.count();
        long totalLocations = locationRepository.count();
        long totalPayments = paymentRepository.count();
        long totalReviews = reviewRepository.count();

        BigDecimal totalRevenue = sumField("payments",
                Criteria.where("status").is(PaymentStatus.PAID.name()),
                "amount");

        return AdminOverviewResponse.builder()
                .totalVehicles(totalVehicles)
                .availableVehicles(availableVehicles)
                .totalBookings(totalBookings)
                .activeBookings(activeBookings)
                .totalCustomers(totalCustomers)
                .totalLocations(totalLocations)
                .totalPayments(totalPayments)
                .totalReviews(totalReviews)
                .totalRevenue(totalRevenue)
                .build();
    }

    @Override
    public AdminDashboardResponse getDashboard(int windowDays) {
        int days = Math.max(MIN_DASHBOARD_WINDOW_DAYS, Math.min(windowDays, MAX_DASHBOARD_WINDOW_DAYS));
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime windowStart = now.minusDays(days);
        LocalDateTime previousWindowStart = windowStart.minusDays(days);

        long totalVehicles = vehicleRepository.count();
        long availableVehicles = vehicleRepository.countByStatus(VehicleStatus.AVAILABLE);
        long reservedVehicles = vehicleRepository.countByStatus(VehicleStatus.RESERVED);
        long rentedVehicles = vehicleRepository.countByStatus(VehicleStatus.RENTED);
        long maintenanceVehicles = vehicleRepository.countByStatus(VehicleStatus.MAINTENANCE);

        long totalBookings = bookingRepository.count();
        long activeBookings = bookingRepository.countByStatus(BookingStatus.ACTIVE);
        long bookingsInWindow = count("bookings", rangeCriteria("created_date", windowStart, now));
        long completedInWindow = count("bookings",
                rangeCriteria("created_date", windowStart, now),
                Criteria.where("status").is(BookingStatus.COMPLETED.name()));
        long cancelledInWindow = count("bookings",
                rangeCriteria("created_date", windowStart, now),
                Criteria.where("status").is(BookingStatus.CANCELED.name()));
        long overdueActiveBookings = count("bookings",
                Criteria.where("status").is(BookingStatus.ACTIVE.name()),
                Criteria.where("end_date").lt(LocalDate.now()));

        long totalCustomers = userRepository.count();
        long newCustomersInWindow = count("users", rangeCriteria("created_date", windowStart, now));
        double repeatCustomerRatePercent = calculateRepeatCustomerRatePercent();

        long totalPayments = paymentRepository.count();
        long paidPayments = count("payments", Criteria.where("status").is(PaymentStatus.PAID.name()));
        long failedPayments = count("payments", Criteria.where("status").is(PaymentStatus.FAILED.name()));
        long paidPaymentsInWindow = count("payments",
                rangeCriteria("created_date", windowStart, now),
                Criteria.where("status").is(PaymentStatus.PAID.name()));
        long failedPaymentsInWindow = count("payments",
                rangeCriteria("created_date", windowStart, now),
                Criteria.where("status").is(PaymentStatus.FAILED.name()));

        BigDecimal revenueTotal = sumField("payments",
                Criteria.where("status").is(PaymentStatus.PAID.name()),
                "amount");
        BigDecimal revenueInWindow = sumField("payments",
                Criteria.where("status").is(PaymentStatus.PAID.name())
                        .and("paid_at").gte(windowStart).lt(now),
                "amount");
        BigDecimal revenuePreviousWindow = sumField("payments",
                Criteria.where("status").is(PaymentStatus.PAID.name())
                        .and("paid_at").gte(previousWindowStart).lt(windowStart),
                "amount");

        BigDecimal outstandingReceivables = calculateOutstandingReceivables();

        long totalReviews = reviewRepository.count();
        long pendingReviews = count("reviews", Criteria.where("status").is(ReviewStatus.PENDING.name()));
        long approvedReviews = count("reviews", Criteria.where("status").is(ReviewStatus.APPROVED.name()));
        double averageApprovedRating = calculateAverageApprovedRating();

        long excursionBookingsInWindow = count("excursion_bookings", rangeCriteria("booked_at", windowStart, now));
        long circuitBookingsInWindow = count("circuit_bookings", rangeCriteria("booked_at", windowStart, now));
        long vehicleBookingsInWindow = bookingsInWindow;

        double completionRatePercent = percentage(completedInWindow, bookingsInWindow);
        double cancellationRatePercent = percentage(cancelledInWindow, bookingsInWindow);
        double utilizationRatePercent = percentage(reservedVehicles + rentedVehicles, totalVehicles);
        double availabilityRatePercent = percentage(availableVehicles, totalVehicles);
        double paymentSuccessRatePercent = percentage(paidPaymentsInWindow, paidPaymentsInWindow + failedPaymentsInWindow);
        double revenueGrowthPercent = growthPercent(revenueInWindow, revenuePreviousWindow);

        List<String> alerts = buildAlerts(cancellationRatePercent, paymentSuccessRatePercent, availabilityRatePercent, pendingReviews);

        return AdminDashboardResponse.builder()
                .generatedAt(now)
                .windowDays(days)
                .snapshot(AdminDashboardResponse.Snapshot.builder()
                        .totalRevenuePaid(scaleMoney(revenueTotal))
                        .revenueInWindow(scaleMoney(revenueInWindow))
                        .revenuePreviousWindow(scaleMoney(revenuePreviousWindow))
                        .revenueGrowthPercent(scaleRate(revenueGrowthPercent))
                        .build())
                .booking(AdminDashboardResponse.BookingMetrics.builder()
                        .totalBookings(totalBookings)
                        .bookingsInWindow(bookingsInWindow)
                        .activeBookings(activeBookings)
                        .overdueActiveBookings(overdueActiveBookings)
                        .completionRatePercent(scaleRate(completionRatePercent))
                        .cancellationRatePercent(scaleRate(cancellationRatePercent))
                        .build())
                .fleet(AdminDashboardResponse.FleetMetrics.builder()
                        .totalVehicles(totalVehicles)
                        .availableVehicles(availableVehicles)
                        .reservedVehicles(reservedVehicles)
                        .rentedVehicles(rentedVehicles)
                        .maintenanceVehicles(maintenanceVehicles)
                        .utilizationRatePercent(scaleRate(utilizationRatePercent))
                        .availabilityRatePercent(scaleRate(availabilityRatePercent))
                        .build())
                .customer(AdminDashboardResponse.CustomerMetrics.builder()
                        .totalCustomers(totalCustomers)
                        .newCustomersInWindow(newCustomersInWindow)
                        .repeatCustomerRatePercent(scaleRate(repeatCustomerRatePercent))
                        .build())
                .payment(AdminDashboardResponse.PaymentMetrics.builder()
                        .totalPayments(totalPayments)
                        .paidPayments(paidPayments)
                        .failedPayments(failedPayments)
                        .paymentSuccessRatePercent(scaleRate(paymentSuccessRatePercent))
                        .outstandingReceivables(scaleMoney(outstandingReceivables))
                        .build())
                .experience(AdminDashboardResponse.ExperienceMetrics.builder()
                        .totalReviews(totalReviews)
                        .pendingReviews(pendingReviews)
                        .approvedReviews(approvedReviews)
                        .averageApprovedRating(scaleRate(averageApprovedRating))
                        .build())
                .demand(AdminDashboardResponse.DemandMetrics.builder()
                        .vehicleBookingsInWindow(vehicleBookingsInWindow)
                        .excursionBookingsInWindow(excursionBookingsInWindow)
                        .circuitBookingsInWindow(circuitBookingsInWindow)
                        .dominantChannel(dominantChannel(vehicleBookingsInWindow, excursionBookingsInWindow, circuitBookingsInWindow))
                        .build())
                .alerts(alerts)
                .build();
    }

    private List<String> buildAlerts(double cancellationRatePercent,
                                     double paymentSuccessRatePercent,
                                     double availabilityRatePercent,
                                     long pendingReviews) {
        List<String> alerts = new ArrayList<>();
        if (cancellationRatePercent >= 25.0d) {
            alerts.add("High cancellation rate in current window. Review pricing, availability accuracy, and booking UX.");
        }
        if (paymentSuccessRatePercent > 0 && paymentSuccessRatePercent < 90.0d) {
            alerts.add("Payment success rate is below 90%. Check payment provider errors and failed checkout events.");
        }
        if (availabilityRatePercent < 20.0d) {
            alerts.add("Fleet availability is below 20%. Consider reallocating vehicles or reducing maintenance overlap.");
        }
        if (pendingReviews > 50) {
            alerts.add("Review moderation backlog is high. Prioritize pending reviews to protect rating trust.");
        }
        return alerts;
    }

    private String dominantChannel(long vehicleBookings, long excursionBookings, long circuitBookings) {
        if (vehicleBookings >= excursionBookings && vehicleBookings >= circuitBookings) {
            return "VEHICLE_RENTAL";
        }
        if (excursionBookings >= circuitBookings) {
            return "EXCURSION";
        }
        return "CIRCUIT";
    }

    private double calculateRepeatCustomerRatePercent() {
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(new Criteria().andOperator(
                        Criteria.where("user_id").exists(true),
                        Criteria.where("user_id").ne(null),
                        Criteria.where("status").ne(BookingStatus.CANCELED.name())
                )),
                Aggregation.group("user_id").count().as("bookingCount")
        );

        List<Document> groupedCustomers = mongoTemplate.aggregate(aggregation, "bookings", Document.class).getMappedResults();
        if (groupedCustomers.isEmpty()) {
            return 0.0d;
        }
        long uniqueCustomers = groupedCustomers.size();
        long repeatCustomers = groupedCustomers.stream()
                .map(document -> toLong(document.get("bookingCount")))
                .filter(count -> count >= 2)
                .count();
        return percentage(repeatCustomers, uniqueCustomers);
    }

    private double calculateAverageApprovedRating() {
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(new Criteria().andOperator(
                        Criteria.where("status").is(ReviewStatus.APPROVED.name()),
                        Criteria.where("rating").ne(null)
                )),
                Aggregation.group().avg("rating").as("avgRating")
        );
        Document result = mongoTemplate.aggregate(aggregation, "reviews", Document.class).getUniqueMappedResult();
        if (result == null || result.get("avgRating") == null) {
            return 0.0d;
        }
        return toBigDecimal(result.get("avgRating")).doubleValue();
    }

    private BigDecimal calculateOutstandingReceivables() {
        Query query = new Query(Criteria.where("status").ne(BookingStatus.CANCELED.name()));
        query.fields().include("pricing.total").include("paid_amount");

        List<Document> bookings = mongoTemplate.find(query, Document.class, "bookings");
        BigDecimal outstanding = BigDecimal.ZERO;
        for (Document booking : bookings) {
            Object pricingRaw = booking.get("pricing");
            Document pricing = pricingRaw instanceof Document ? (Document) pricingRaw : null;
            BigDecimal total = toBigDecimal(pricing != null ? pricing.get("total") : null);
            BigDecimal paid = toBigDecimal(booking.get("paid_amount"));
            BigDecimal remaining = total.subtract(paid);
            if (remaining.compareTo(BigDecimal.ZERO) > 0) {
                outstanding = outstanding.add(remaining);
            }
        }
        return outstanding;
    }

    private long count(String collection, Criteria... criteria) {
        Query query = new Query();
        if (criteria != null) {
            for (Criteria criterion : criteria) {
                if (criterion != null) {
                    query.addCriteria(criterion);
                }
            }
        }
        return mongoTemplate.count(query, collection);
    }

    private Criteria rangeCriteria(String field, LocalDateTime start, LocalDateTime end) {
        return Criteria.where(field).gte(start).lt(end);
    }

    private BigDecimal sumField(String collection, Criteria criteria, String field) {
        Aggregation aggregation;
        if (criteria == null) {
            aggregation = Aggregation.newAggregation(
                    Aggregation.group().sum(field).as("total")
            );
        } else {
            aggregation = Aggregation.newAggregation(
                    Aggregation.match(criteria),
                    Aggregation.group().sum(field).as("total")
            );
        }

        Document result = mongoTemplate.aggregate(aggregation, collection, Document.class).getUniqueMappedResult();
        if (result == null || result.get("total") == null) {
            return BigDecimal.ZERO;
        }
        return toBigDecimal(result.get("total"));
    }

    private BigDecimal toBigDecimal(Object value) {
        if (value == null) {
            return BigDecimal.ZERO;
        }
        if (value instanceof BigDecimal number) {
            return number;
        }
        if (value instanceof Decimal128 decimal128) {
            return decimal128.bigDecimalValue();
        }
        if (value instanceof Number number) {
            return BigDecimal.valueOf(number.doubleValue());
        }
        return new BigDecimal(String.valueOf(value));
    }

    private long toLong(Object value) {
        if (value == null) {
            return 0L;
        }
        if (value instanceof Number number) {
            return number.longValue();
        }
        return Long.parseLong(String.valueOf(value));
    }

    private double percentage(long numerator, long denominator) {
        if (denominator <= 0) {
            return 0.0d;
        }
        return ((double) numerator * 100.0d) / (double) denominator;
    }

    private double growthPercent(BigDecimal current, BigDecimal previous) {
        if (previous == null || previous.compareTo(BigDecimal.ZERO) == 0) {
            return (current != null && current.compareTo(BigDecimal.ZERO) > 0) ? 100.0d : 0.0d;
        }
        return current.subtract(previous)
                .multiply(BigDecimal.valueOf(100))
                .divide(previous, 4, RoundingMode.HALF_UP)
                .doubleValue();
    }

    private double scaleRate(double value) {
        return BigDecimal.valueOf(value).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }

    private BigDecimal scaleMoney(BigDecimal value) {
        if (value == null) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }
        return value.setScale(2, RoundingMode.HALF_UP);
    }
}
