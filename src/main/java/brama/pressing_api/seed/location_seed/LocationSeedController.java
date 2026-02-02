package brama.pressing_api.seed.location_seed;

import brama.pressing_api.seed.location_seed.domain.LocationSeed;
import brama.pressing_api.seed.location_seed.repository.LocationSeedRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/locations")
@RequiredArgsConstructor
@Tag(name = "Locations", description = "Pickup and dropoff locations")
public class LocationSeedController {
    private final LocationSeedRepository locationRepository;

    /**
     * Get all active locations
     */
    @GetMapping
    @Operation(summary = "Get all active locations")
    public List<LocationSeed> getAllLocations() {
        return locationRepository.findByActiveTrue();
    }

    /**
     * Get location by ID
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get location by ID")
    public LocationSeed getLocationById(@PathVariable String id) {
        return locationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Location not found"));
    }
}
