package brama.pressing_api.booking;

import brama.pressing_api.booking.domain.model.BookingPaymentStatus;
import brama.pressing_api.booking.domain.model.BookingStatus;
import brama.pressing_api.booking.dto.request.AdminCreateBookingRequest;
import brama.pressing_api.booking.dto.request.RecordBookingPaymentRequest;
import brama.pressing_api.booking.dto.request.UpdateBookingStatusRequest;
import brama.pressing_api.booking.dto.response.BookingAdminStatsResponse;
import brama.pressing_api.booking.dto.response.BookingResponse;
import brama.pressing_api.booking.service.BookingService;
import brama.pressing_api.booking.service.BookingSearchCriteria;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

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
    public Page<BookingResponse> listBookings(Pageable pageable,
                                              @RequestParam(required = false) String query,
                                              @RequestParam(required = false) BookingStatus status,
                                              @RequestParam(required = false) BookingPaymentStatus paymentStatus,
                                              @RequestParam(required = false) LocalDate startDate,
                                              @RequestParam(required = false) LocalDate endDate) {
        if (query != null || status != null || paymentStatus != null || startDate != null || endDate != null) {
            BookingSearchCriteria criteria = BookingSearchCriteria.builder()
                    .query(query)
                    .status(status)
                    .paymentStatus(paymentStatus)
                    .startDate(startDate)
                    .endDate(endDate)
                    .build();
            return bookingService.searchAdmin(criteria, pageable);
        }
        return bookingService.listAdmin(pageable);
    }

    /**
     * Creates a booking on behalf of a customer.
     */
    @PostMapping
    public BookingResponse createBooking(@Valid @RequestBody AdminCreateBookingRequest request,
                                         final Authentication authentication) {
        return bookingService.createAdmin(request, getUserId(authentication));
    }

    /**
     * Records a manual payment for a booking.
     */
    @PostMapping("/{id}/payments")
    public BookingResponse recordPayment(@PathVariable String id,
                                         @Valid @RequestBody RecordBookingPaymentRequest request,
                                         final Authentication authentication) {
        return bookingService.recordPayment(id, request, getUserId(authentication));
    }

    /**
     * Returns summary stats for the booking dashboard.
     */
    @GetMapping("/stats")
    public BookingAdminStatsResponse getStats() {
        return bookingService.getAdminStats();
    }

    /**
     * Exports bookings to CSV using the same filters as list.
     */
    @GetMapping("/export")
    public ResponseEntity<String> exportCsv(@RequestParam(required = false) String query,
                                            @RequestParam(required = false) BookingStatus status,
                                            @RequestParam(required = false) BookingPaymentStatus paymentStatus,
                                            @RequestParam(required = false) LocalDate startDate,
                                            @RequestParam(required = false) LocalDate endDate) {
        BookingSearchCriteria criteria = BookingSearchCriteria.builder()
                .query(query)
                .status(status)
                .paymentStatus(paymentStatus)
                .startDate(startDate)
                .endDate(endDate)
                .build();
        String csv = bookingService.exportCsv(criteria);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=bookings.csv")
                .body(csv);
    }

    /**
     * Updates booking status.
     */
    @PatchMapping("/{id}/status")
    public BookingResponse updateStatus(@PathVariable String id,
                                        @Valid @RequestBody UpdateBookingStatusRequest request) {
        return bookingService.updateStatus(id, request.getStatus());
    }

    private String getUserId(final Authentication authentication) {
        if (authentication == null || authentication.getPrincipal() == null) {
            return null;
        }
        return ((brama.pressing_api.user.User) authentication.getPrincipal()).getId();
    }
}
