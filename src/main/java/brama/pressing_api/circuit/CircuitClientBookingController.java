package brama.pressing_api.circuit;

import brama.pressing_api.circuit.dto.request.CreateCircuitBookingRequest;
import brama.pressing_api.circuit.dto.response.CircuitBookingResponse;
import brama.pressing_api.circuit.service.CircuitBookingService;
import brama.pressing_api.user.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/client/circuits")
@RequiredArgsConstructor
public class CircuitClientBookingController {
    private final CircuitBookingService bookingService;

    @PostMapping("/{circuitId}/bookings")
    public ResponseEntity<CircuitBookingResponse> createBooking(
            @PathVariable String circuitId,
            @Valid @RequestBody CreateCircuitBookingRequest request,
            Authentication authentication) {

        // 🔥 Get user ID directly from authentication principal
        User user = (User) authentication.getPrincipal();


        System.out.println("👤 User ID: " + user);




        return ResponseEntity.status(HttpStatus.CREATED)
                .body(bookingService.createPublic(circuitId, request,  authentication));
    }
}
