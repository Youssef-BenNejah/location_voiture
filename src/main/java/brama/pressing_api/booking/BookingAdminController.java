package brama.pressing_api.booking;

import brama.pressing_api.booking.dto.request.UpdateBookingStatusRequest;
import brama.pressing_api.booking.dto.response.BookingResponse;
import brama.pressing_api.booking.service.BookingService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Admin endpoints to list and update booking status.
 */
@RestController
@RequestMapping("/api/v1/admin/bookings")
@RequiredArgsConstructor
@Tag(name = "Bookings - Admin", description = "Admin booking management")
@PreAuthorize("hasRole('ADMIN')")
public class BookingAdminController {
    private final BookingService bookingService;

    /**
     * Lists bookings with pagination.
     */
    @GetMapping
    public Page<BookingResponse> listBookings(Pageable pageable) {
        return bookingService.listAdmin(pageable);
    }

    /**
     * Updates booking status.
     */
    @PatchMapping("/{id}/status")
    public BookingResponse updateStatus(@PathVariable String id,
                                        @Valid @RequestBody UpdateBookingStatusRequest request) {
        return bookingService.updateStatus(id, request.getStatus());
    }
}
