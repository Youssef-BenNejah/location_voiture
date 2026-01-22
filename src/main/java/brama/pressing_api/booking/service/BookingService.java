package brama.pressing_api.booking.service;

import brama.pressing_api.booking.domain.model.BookingStatus;
import brama.pressing_api.booking.dto.request.CreateBookingRequest;
import brama.pressing_api.booking.dto.response.BookingResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface BookingService {
    BookingResponse create(CreateBookingRequest request);

    List<BookingResponse> listMyBookings();

    BookingResponse getMyBooking(String bookingId);

    BookingResponse cancelMyBooking(String bookingId);

    Page<BookingResponse> listAdmin(Pageable pageable);

    BookingResponse updateStatus(String bookingId, BookingStatus status);
}
