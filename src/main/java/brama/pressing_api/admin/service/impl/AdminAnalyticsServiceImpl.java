package brama.pressing_api.admin.service.impl;

import brama.pressing_api.admin.dto.response.AdminOverviewResponse;
import brama.pressing_api.admin.service.AdminAnalyticsService;
import brama.pressing_api.booking.domain.model.BookingStatus;
import brama.pressing_api.booking.repo.BookingRepository;
import brama.pressing_api.location.repo.LocationRepository;
import brama.pressing_api.payment.domain.model.Payment;
import brama.pressing_api.payment.domain.model.PaymentStatus;
import brama.pressing_api.payment.repo.PaymentRepository;
import brama.pressing_api.review.repo.ReviewRepository;
import brama.pressing_api.user.UserRepository;
import brama.pressing_api.vehicle.domain.model.VehicleStatus;
import brama.pressing_api.vehicle.repo.VehicleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class AdminAnalyticsServiceImpl implements AdminAnalyticsService {
    private final VehicleRepository vehicleRepository;
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final LocationRepository locationRepository;
    private final PaymentRepository paymentRepository;
    private final ReviewRepository reviewRepository;

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

        BigDecimal totalRevenue = paymentRepository.findByStatus(PaymentStatus.PAID)
                .stream()
                .map(Payment::getAmount)
                .filter(amount -> amount != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

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
}
