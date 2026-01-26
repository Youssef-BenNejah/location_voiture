package brama.pressing_api.circuit;

import brama.pressing_api.circuit.dto.request.CreateCircuitBookingRequest;
import brama.pressing_api.circuit.dto.response.CircuitBookingResponse;
import brama.pressing_api.circuit.dto.response.CircuitResponse;
import brama.pressing_api.circuit.service.CircuitBookingService;
import brama.pressing_api.circuit.service.CircuitSearchCriteria;
import brama.pressing_api.circuit.service.CircuitService;
import brama.pressing_api.utils.SecurityUtils;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/v1/public/circuits")
@RequiredArgsConstructor
@Tag(name = "Circuits - Public", description = "Public circuit listings")
public class CircuitPublicController {
    private final CircuitService circuitService;
    private final CircuitBookingService bookingService;

    @GetMapping
    public Page<CircuitResponse> listCircuits(Pageable pageable,
                                              @RequestParam(required = false) String query,
                                              @RequestParam(required = false) String origin,
                                              @RequestParam(required = false) String destination,
                                              @RequestParam(required = false) brama.pressing_api.circuit.domain.CircuitVehicleType vehicle,
                                              @RequestParam(required = false) BigDecimal minPrice,
                                              @RequestParam(required = false) BigDecimal maxPrice) {
        CircuitSearchCriteria criteria = CircuitSearchCriteria.builder()
                .query(query)
                .originCity(origin)
                .destinationCity(destination)
                .vehicleType(vehicle)
                .minPrice(minPrice)
                .maxPrice(maxPrice)
                .build();
        return circuitService.listPublic(criteria, pageable);
    }

    @GetMapping("/{id}")
    public CircuitResponse getCircuit(@PathVariable String id) {
        return circuitService.getPublic(id);
    }

    @PostMapping("/{id}/bookings")
    public ResponseEntity<CircuitBookingResponse> createBooking(@PathVariable String id,
                                                                @Valid @RequestBody CreateCircuitBookingRequest request,
                                                                final Authentication authentication) {
        String userId = SecurityUtils.getCurrentUserId().orElse(null);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(bookingService.createPublic(id, request, userId));
    }
}
