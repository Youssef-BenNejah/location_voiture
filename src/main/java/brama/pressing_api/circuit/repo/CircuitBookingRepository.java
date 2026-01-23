package brama.pressing_api.circuit.repo;

import brama.pressing_api.circuit.domain.CircuitBooking;
import brama.pressing_api.circuit.domain.CircuitBookingStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface CircuitBookingRepository extends MongoRepository<CircuitBooking, String>, CircuitBookingRepositoryCustom {
    List<CircuitBooking> findByStatus(CircuitBookingStatus status);

    Page<CircuitBooking> findByUserId(String userId, Pageable pageable);

    long countByCircuitId(String circuitId);

    List<CircuitBooking> findByUserIdIsNullAndCustomerEmailIgnoreCase(String email);
}
