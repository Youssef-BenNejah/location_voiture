package brama.pressing_api.excursionbooking;

import brama.pressing_api.excursionbooking.domain.model.ExcursionBookingStatus;
import brama.pressing_api.excursionbooking.dto.request.UpdateExcursionBookingStatusRequest;
import brama.pressing_api.excursionbooking.dto.response.ExcursionBookingResponse;
import brama.pressing_api.excursionbooking.service.ExcursionBookingSearchCriteria;
import brama.pressing_api.excursionbooking.service.ExcursionBookingService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

/**
 * Admin endpoints to manage excursion bookings.
 */
@RestController
@RequestMapping("/api/v1/admin/excursion-bookings")
@RequiredArgsConstructor
@Tag(name = "Excursion Bookings - Admin", description = "Admin excursion bookings")
@PreAuthorize("hasRole('ADMIN')")
public class ExcursionBookingAdminController {
    private final ExcursionBookingService bookingService;

    /**
     * Lists excursion bookings with filters and pagination.
     */
    @GetMapping
    public Page<ExcursionBookingResponse> listBookings(@RequestParam(required = false) String q,
                                                       @RequestParam(required = false) ExcursionBookingStatus status,
                                                       @RequestParam(required = false) String excursionId,
                                                       @RequestParam(required = false)
                                                       @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
                                                       LocalDate selectedDate,
                                                       Pageable pageable) {
        ExcursionBookingSearchCriteria criteria = ExcursionBookingSearchCriteria.builder()
                .query(q)
                .status(status)
                .excursionId(excursionId)
                .selectedDate(selectedDate)
                .build();
        return bookingService.listAdmin(criteria, pageable);
    }

    /**
     * Returns a single booking by id.
     */
    @GetMapping("/{id}")
    public ExcursionBookingResponse getBooking(@PathVariable String id) {
        return bookingService.getAdminBooking(id);
    }

    /**
     * Updates the status of a booking.
     */
    @PutMapping("/{id}/status")
    public ExcursionBookingResponse updateStatus(@PathVariable String id,
                                                 @Valid @RequestBody UpdateExcursionBookingStatusRequest request) {
        return bookingService.updateStatus(id, request.getStatus());
    }

    /**
     * Exports bookings to CSV using the same filters as list.
     */
    @GetMapping("/export")
    public ResponseEntity<String> exportCsv(@RequestParam(required = false) String q,
                                            @RequestParam(required = false) ExcursionBookingStatus status,
                                            @RequestParam(required = false) String excursionId,
                                            @RequestParam(required = false)
                                            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
                                            LocalDate selectedDate) {
        ExcursionBookingSearchCriteria criteria = ExcursionBookingSearchCriteria.builder()
                .query(q)
                .status(status)
                .excursionId(excursionId)
                .selectedDate(selectedDate)
                .build();
        String csv = bookingService.exportCsv(criteria);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=excursion-bookings.csv")
                .contentType(MediaType.valueOf("text/csv"))
                .body(csv);
    }

    /**
     * Sends a confirmation email for a booking.
     */
    @PostMapping("/{id}/send-confirmation")
    public ResponseEntity<Void> sendConfirmation(@PathVariable String id) {
        bookingService.sendConfirmation(id);
        return ResponseEntity.accepted().build();
    }
}
