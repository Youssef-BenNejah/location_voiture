package brama.pressing_api.excursionbooking.repo;

import brama.pressing_api.excursionbooking.domain.model.ExcursionBooking;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface ExcursionBookingRepository extends MongoRepository<ExcursionBooking, String>, ExcursionBookingRepositoryCustom {
    List<ExcursionBooking> findByUserId(String userId);

    List<ExcursionBooking> findByUserIdIsNullAndCustomerEmailIgnoreCase(String customerEmail);

    Optional<ExcursionBooking> findByIdAndUserId(String id, String userId);
}
