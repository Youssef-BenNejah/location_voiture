package brama.pressing_api.location;

import brama.pressing_api.location.dto.response.LocationResponse;
import brama.pressing_api.location.service.LocationService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Public location listing endpoint.
 */
@RestController
@RequestMapping("/api/v1/public/locations")
@RequiredArgsConstructor
@Tag(name = "Locations - Public", description = "Public location listing")
public class LocationPublicController {
    private final LocationService locationService;

    /**
     * Returns active pickup/dropoff locations.
     */
    @GetMapping
    public List<LocationResponse> listLocations() {
        return locationService.listPublic();
    }
}
