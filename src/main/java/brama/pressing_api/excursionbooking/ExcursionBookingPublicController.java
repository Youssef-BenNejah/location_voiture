package brama.pressing_api.excursionbooking;

import brama.pressing_api.excursionbooking.dto.request.CreateExcursionBookingRequest;
import brama.pressing_api.excursionbooking.dto.response.ExcursionBookingResponse;
import brama.pressing_api.excursionbooking.service.ExcursionBookingService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Public endpoint to create excursion bookings from the excursion details page.
 */
@RestController
@RequestMapping("/api/v1/client/excursions")
@RequiredArgsConstructor
@Tag(name = "Excursion Bookings - Public", description = "Public excursion booking")
public class ExcursionBookingPublicController {
    private final ExcursionBookingService bookingService;

    /**
     * Creates a booking for an excursion and reserves capacity.
     */
    @PostMapping("/{excursionId}/bookings")
    public ResponseEntity<ExcursionBookingResponse> createBooking(
            @PathVariable String excursionId,
            @Valid @RequestBody CreateExcursionBookingRequest request,
            Authentication authentication) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(bookingService.createPublic(excursionId, request, authentication));
    }
}
