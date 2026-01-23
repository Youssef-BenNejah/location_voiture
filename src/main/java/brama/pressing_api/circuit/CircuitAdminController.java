package brama.pressing_api.circuit;

import brama.pressing_api.circuit.dto.request.CircuitRequest;
import brama.pressing_api.circuit.dto.response.CircuitAdminStatsResponse;
import brama.pressing_api.circuit.dto.response.CircuitResponse;
import brama.pressing_api.circuit.service.CircuitSearchCriteria;
import brama.pressing_api.circuit.service.CircuitService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/v1/admin/circuits")
@RequiredArgsConstructor
@Tag(name = "Circuits - Admin", description = "Admin circuit management")
@PreAuthorize("hasRole('ADMIN')")
public class CircuitAdminController {
    private final CircuitService circuitService;

    @GetMapping
    public Page<CircuitResponse> list(Pageable pageable,
                                      @RequestParam(required = false) String query,
                                      @RequestParam(required = false) String origin,
                                      @RequestParam(required = false) String destination,
                                      @RequestParam(required = false) brama.pressing_api.circuit.domain.CircuitVehicleType vehicle,
                                      @RequestParam(required = false) BigDecimal minPrice,
                                      @RequestParam(required = false) BigDecimal maxPrice,
                                      @RequestParam(required = false) brama.pressing_api.circuit.domain.CircuitStatus status) {
        CircuitSearchCriteria criteria = CircuitSearchCriteria.builder()
                .query(query)
                .originCity(origin)
                .destinationCity(destination)
                .vehicleType(vehicle)
                .minPrice(minPrice)
                .maxPrice(maxPrice)
                .status(status)
                .build();
        return circuitService.listAdmin(criteria, pageable);
    }

    @GetMapping("/stats")
    public CircuitAdminStatsResponse getStats() {
        return circuitService.getAdminStats();
    }

    @PostMapping
    public CircuitResponse create(@Valid @RequestBody CircuitRequest request) {
        return circuitService.create(request);
    }

    @PutMapping("/{id}")
    public CircuitResponse update(@PathVariable String id, @Valid @RequestBody CircuitRequest request) {
        return circuitService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable String id) {
        circuitService.delete(id);
    }
}
