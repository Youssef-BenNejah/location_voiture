package brama.pressing_api.booking.service;

import brama.pressing_api.booking.domain.model.BookingStatus;
import brama.pressing_api.booking.dto.request.AdminCreateBookingRequest;
import brama.pressing_api.booking.dto.request.CreateBookingRequest;
import brama.pressing_api.booking.dto.request.RecordBookingPaymentRequest;
import brama.pressing_api.booking.dto.response.BookingAdminStatsResponse;
import brama.pressing_api.booking.dto.response.BookingClientResponse;
import brama.pressing_api.booking.dto.response.BookingResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface BookingService {
    BookingResponse create(CreateBookingRequest request);

    List<BookingClientResponse> listMyBookings();

    BookingResponse getMyBooking(String bookingId);

    BookingClientResponse cancelMyBooking(String bookingId);

    Page<BookingResponse> listAdmin(Pageable pageable);

    Page<BookingResponse> searchAdmin(BookingSearchCriteria criteria, Pageable pageable);

    BookingResponse createAdmin(AdminCreateBookingRequest request, String adminId);

    BookingResponse recordPayment(String bookingId, RecordBookingPaymentRequest request, String adminId);

    BookingAdminStatsResponse getAdminStats();

    String exportCsv(BookingSearchCriteria criteria);

    BookingResponse updateStatus(String bookingId, BookingStatus status);
}
