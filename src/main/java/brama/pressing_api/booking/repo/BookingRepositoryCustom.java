package brama.pressing_api.booking.repo;

import brama.pressing_api.booking.domain.model.Booking;
import brama.pressing_api.booking.service.BookingSearchCriteria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BookingRepositoryCustom {
    Page<Booking> search(BookingSearchCriteria criteria, Pageable pageable);
}
