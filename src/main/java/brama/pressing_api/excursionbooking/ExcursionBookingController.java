package brama.pressing_api.excursionbooking;

import brama.pressing_api.excursionbooking.dto.response.ExcursionBookingResponse;
import brama.pressing_api.excursionbooking.dto.response.ExcursionBookingTicketResponse;
import brama.pressing_api.excursionbooking.service.ExcursionBookingService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Authenticated endpoints for customers to manage their excursion bookings.
 */
@RestController
@RequestMapping("/api/v1/excursions/bookings")
@RequiredArgsConstructor
@Tag(name = "Excursion Bookings", description = "Customer excursion bookings")
public class ExcursionBookingController {
    private final ExcursionBookingService bookingService;

    /**
     * Lists the current user's excursion bookings.
     */
    @GetMapping
    public List<ExcursionBookingResponse> listMyBookings() {
        return bookingService.listMyBookings();
    }

    /**
     * Returns a single booking owned by the current user.
     */
    @GetMapping("/{id}")
    public ExcursionBookingResponse getMyBooking(@PathVariable String id) {
        return bookingService.getMyBooking(id);
    }

    /**
     * Cancels a booking owned by the current user.
     */
    @PostMapping("/{id}/cancel")
    public ExcursionBookingResponse cancelMyBooking(@PathVariable String id) {
        return bookingService.cancelMyBooking(id);
    }

    /**
     * Returns a ticket payload for download or display.
     */
    @GetMapping("/{id}/ticket")
    public ExcursionBookingTicketResponse getMyTicket(@PathVariable String id) {
        return bookingService.getMyTicket(id);
    }
}
