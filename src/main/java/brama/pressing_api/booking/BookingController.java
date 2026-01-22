package brama.pressing_api.booking;

import brama.pressing_api.booking.dto.request.CreateBookingRequest;
import brama.pressing_api.booking.dto.response.BookingResponse;
import brama.pressing_api.booking.service.BookingService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Customer booking endpoints (create, list, view, cancel).
 */
@RestController
@RequestMapping("/api/v1/bookings")
@RequiredArgsConstructor
@Tag(name = "Bookings", description = "Customer bookings")
public class BookingController {
    private final BookingService bookingService;

    /**
     * Creates a booking for the authenticated user.
     */
    @PostMapping
    public ResponseEntity<BookingResponse> createBooking(@Valid @RequestBody CreateBookingRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(bookingService.create(request));
    }

    /**
     * Lists the authenticated user's bookings.
     */
    @GetMapping
    public List<BookingResponse> listMyBookings() {
        return bookingService.listMyBookings();
    }

    /**
     * Returns a single booking owned by the authenticated user.
     */
    @GetMapping("/{id}")
    public BookingResponse getMyBooking(@PathVariable String id) {
        return bookingService.getMyBooking(id);
    }

    /**
     * Cancels a booking owned by the authenticated user.
     */
    @PostMapping("/{id}/cancel")
    public BookingResponse cancelBooking(@PathVariable String id) {
        return bookingService.cancelMyBooking(id);
    }
}
