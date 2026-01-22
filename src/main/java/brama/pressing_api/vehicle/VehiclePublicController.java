package brama.pressing_api.vehicle;

import brama.pressing_api.vehicle.domain.model.FuelType;
import brama.pressing_api.vehicle.domain.model.TransmissionType;
import brama.pressing_api.vehicle.domain.model.VehicleCategory;
import brama.pressing_api.vehicle.dto.response.VehicleResponse;
import brama.pressing_api.vehicle.service.VehicleSearchCriteria;
import brama.pressing_api.vehicle.service.VehicleService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Public vehicle search and detail endpoints.
 */
@RestController
@RequestMapping("/api/v1/public/vehicles")
@RequiredArgsConstructor
@Tag(name = "Vehicles - Public", description = "Public vehicle search and details")
public class VehiclePublicController {
    private final VehicleService vehicleService;

    /**
     * Searches vehicles by filters and availability dates.
     */
    @GetMapping
    public Page<VehicleResponse> searchVehicles(
            @RequestParam(required = false) String locationId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) VehicleCategory category,
            @RequestParam(required = false) TransmissionType transmission,
            @RequestParam(required = false) FuelType fuelType,
            @RequestParam(required = false) Integer minSeats,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            Pageable pageable) {
        VehicleSearchCriteria criteria = VehicleSearchCriteria.builder()
                .locationId(locationId)
                .startDate(startDate)
                .endDate(endDate)
                .category(category)
                .transmission(transmission)
                .fuelType(fuelType)
                .minSeats(minSeats)
                .minPrice(minPrice)
                .maxPrice(maxPrice)
                .build();
        return vehicleService.searchPublic(criteria, pageable);
    }

    /**
     * Returns a single vehicle by id.
     */
    @GetMapping("/{id}")
    public VehicleResponse getVehicleById(@PathVariable String id) {
        return vehicleService.getById(id);
    }
}
