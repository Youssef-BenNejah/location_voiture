package brama.pressing_api.excursionbooking.repo;

import brama.pressing_api.excursionbooking.domain.model.ExcursionBooking;
import brama.pressing_api.excursionbooking.service.ExcursionBookingSearchCriteria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ExcursionBookingRepositoryCustom {
    Page<ExcursionBooking> search(ExcursionBookingSearchCriteria criteria, Pageable pageable);
}
