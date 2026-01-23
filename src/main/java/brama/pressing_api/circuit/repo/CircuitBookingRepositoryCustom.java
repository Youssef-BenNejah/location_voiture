package brama.pressing_api.circuit.repo;

import brama.pressing_api.circuit.domain.CircuitBooking;
import brama.pressing_api.circuit.service.CircuitBookingSearchCriteria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CircuitBookingRepositoryCustom {
    Page<CircuitBooking> search(CircuitBookingSearchCriteria criteria, Pageable pageable);
}
