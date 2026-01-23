package brama.pressing_api.circuit;

import brama.pressing_api.circuit.domain.CircuitBookingStatus;
import brama.pressing_api.circuit.dto.request.UpdateCircuitBookingStatusRequest;
import brama.pressing_api.circuit.dto.response.CircuitBookingAdminStatsResponse;
import brama.pressing_api.circuit.dto.response.CircuitBookingResponse;
import brama.pressing_api.circuit.service.CircuitBookingSearchCriteria;
import brama.pressing_api.circuit.service.CircuitBookingService;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/admin/circuit-bookings")
@RequiredArgsConstructor
@Tag(name = "Circuit Bookings - Admin", description = "Admin circuit bookings")
@PreAuthorize("hasRole('ADMIN')")
public class CircuitBookingAdminController {
    private final CircuitBookingService bookingService;

    @GetMapping
    public Page<CircuitBookingResponse> list(Pageable pageable,
                                             @RequestParam(required = false) String query,
                                             @RequestParam(required = false) CircuitBookingStatus status,
                                             @RequestParam(required = false) String circuitId,
                                             @RequestParam(required = false) LocalDate selectedDate) {
        CircuitBookingSearchCriteria criteria = CircuitBookingSearchCriteria.builder()
                .query(query)
                .status(status)
                .circuitId(circuitId)
                .selectedDate(selectedDate)
                .build();
        return bookingService.listAdmin(criteria, pageable);
    }

    @GetMapping("/{id}")
    public CircuitBookingResponse get(@PathVariable String id) {
        return bookingService.getAdmin(id);
    }

    @GetMapping("/stats")
    public CircuitBookingAdminStatsResponse getStats() {
        return bookingService.getAdminStats();
    }

    @PatchMapping("/{id}/status")
    public CircuitBookingResponse updateStatus(@PathVariable String id,
                                               @Valid @RequestBody UpdateCircuitBookingStatusRequest request) {
        return bookingService.updateStatus(id, request);
    }
}
