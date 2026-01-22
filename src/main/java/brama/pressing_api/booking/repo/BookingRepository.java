package brama.pressing_api.booking.repo;

import brama.pressing_api.booking.domain.model.Booking;
import brama.pressing_api.booking.domain.model.BookingStatus;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends MongoRepository<Booking, String> {
    List<Booking> findByUserId(String userId);

    Optional<Booking> findByIdAndUserId(String id, String userId);

    long countByStatus(BookingStatus status);

    @Query(value = "{ 'start_date': { $lte: ?1 }, 'end_date': { $gte: ?0 }, 'status': { $in: ?2 } }")
    List<Booking> findByOverlappingDates(LocalDate startDate, LocalDate endDate, List<BookingStatus> statuses);
}
