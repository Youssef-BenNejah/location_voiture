package brama.pressing_api.location;

import brama.pressing_api.location.dto.request.CreateLocationRequest;
import brama.pressing_api.location.dto.request.UpdateLocationRequest;
import brama.pressing_api.location.dto.response.LocationResponse;
import brama.pressing_api.location.service.LocationService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Admin endpoints to manage locations.
 */
@RestController
@RequestMapping("/api/v1/admin/locations")
@RequiredArgsConstructor
@Tag(name = "Locations - Admin", description = "Admin location management")
@PreAuthorize("hasRole('ADMIN')")
public class LocationAdminController {
    private final LocationService locationService;

    /**
     * Lists locations with pagination.
     */
    @GetMapping
    public Page<LocationResponse> listLocations(Pageable pageable) {
        return locationService.listAdmin(pageable);
    }

    /**
     * Returns a location by id.
     */
    @GetMapping("/{id}")
    public LocationResponse getLocation(@PathVariable String id) {
        return locationService.getById(id);
    }

    /**
     * Creates a new location.
     */
    @PostMapping
    public ResponseEntity<LocationResponse> createLocation(@Valid @RequestBody CreateLocationRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(locationService.create(request));
    }

    /**
     * Updates a location by id.
     */
    @PutMapping("/{id}")
    public LocationResponse updateLocation(@PathVariable String id, @Valid @RequestBody UpdateLocationRequest request) {
        return locationService.update(id, request);
    }

    /**
     * Deletes a location by id.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLocation(@PathVariable String id) {
        locationService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
