package brama.pressing_api.circuit.service;

import brama.pressing_api.circuit.dto.request.CreateCircuitBookingRequest;
import brama.pressing_api.circuit.dto.request.UpdateCircuitBookingStatusRequest;
import brama.pressing_api.circuit.dto.response.CircuitBookingAdminStatsResponse;
import brama.pressing_api.circuit.dto.response.CircuitBookingResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;

public interface CircuitBookingService {
    CircuitBookingResponse createPublic(String circuitId, CreateCircuitBookingRequest request,  Authentication authentication);

    Page<CircuitBookingResponse> listAdmin(CircuitBookingSearchCriteria criteria, Pageable pageable);

    CircuitBookingResponse updateStatus(String bookingId, UpdateCircuitBookingStatusRequest request);

    CircuitBookingResponse getAdmin(String bookingId);

    Page<CircuitBookingResponse> listMyBookings(String userId, Pageable pageable);

    CircuitBookingResponse getMyBooking(String bookingId, String userId);

    CircuitBookingResponse cancelMyBooking(String bookingId, String userId);

    CircuitBookingResponse getMyTicket(String bookingId, String userId);

    CircuitBookingAdminStatsResponse getAdminStats();
}
