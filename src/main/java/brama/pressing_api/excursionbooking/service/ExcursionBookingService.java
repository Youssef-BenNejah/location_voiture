package brama.pressing_api.excursionbooking.service;

import brama.pressing_api.excursionbooking.domain.model.ExcursionBookingStatus;
import brama.pressing_api.excursionbooking.dto.request.CreateExcursionBookingRequest;
import brama.pressing_api.excursionbooking.dto.response.ExcursionBookingResponse;
import brama.pressing_api.excursionbooking.dto.response.ExcursionBookingTicketResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;

import java.util.List;

public interface ExcursionBookingService {
    ExcursionBookingResponse createPublic(String excursionId, CreateExcursionBookingRequest request, final Authentication authentication);

    List<ExcursionBookingResponse> listMyBookings();

    ExcursionBookingResponse getMyBooking(String bookingId);

    ExcursionBookingResponse cancelMyBooking(String bookingId);

    ExcursionBookingTicketResponse getMyTicket(String bookingId);

    Page<ExcursionBookingResponse> listAdmin(ExcursionBookingSearchCriteria criteria, Pageable pageable);

    ExcursionBookingResponse getAdminBooking(String bookingId);

    ExcursionBookingResponse updateStatus(String bookingId, ExcursionBookingStatus status);

    String exportCsv(ExcursionBookingSearchCriteria criteria);

    void sendConfirmation(String bookingId);
}
