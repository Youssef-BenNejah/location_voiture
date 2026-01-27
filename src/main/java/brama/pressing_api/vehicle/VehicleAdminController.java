package brama.pressing_api.vehicle;

import brama.pressing_api.vehicle.dto.request.CreateVehicleRequest;
import brama.pressing_api.vehicle.dto.request.UpdateVehicleRequest;
import brama.pressing_api.vehicle.dto.response.VehicleResponse;
import brama.pressing_api.vehicle.domain.model.VehicleCategory;
import brama.pressing_api.vehicle.domain.model.VehicleStatus;
import brama.pressing_api.vehicle.service.VehicleSearchCriteria;
import brama.pressing_api.vehicle.service.VehicleService;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
/**
 * Admin endpoints to manage vehicles.
 */
@RestController
@RequestMapping("/api/v1/admin/vehicles")
@RequiredArgsConstructor
@Tag(name = "Vehicles - Admin", description = "Admin vehicle management")
@PreAuthorize("hasRole('ADMIN')")
public class VehicleAdminController {
    private final VehicleService vehicleService;

    /**
     * Lists vehicles with pagination.
     */
    @GetMapping
    public Page<VehicleResponse> listVehicles(
            @RequestParam(required = false) VehicleStatus status,
            @RequestParam(required = false) VehicleCategory category,
            @RequestParam(required = false) String search,
            Pageable pageable) {
        VehicleSearchCriteria criteria = VehicleSearchCriteria.builder()
                .statuses(status != null ? Set.of(status) : null)
                .category(category)
                .search(search)
                .build();
        return vehicleService.listAdmin(criteria, pageable);
    }

    /**
     * Returns a vehicle by id.
     */
    @GetMapping("/{id}")
    public VehicleResponse getVehicle(@PathVariable String id) {
        return vehicleService.getById(id);
    }

    /**
     * Creates a new vehicle.
     */
    @PostMapping
    public ResponseEntity<VehicleResponse> createVehicle(@Valid @RequestBody CreateVehicleRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(vehicleService.create(request));
    }

    /**
     * Updates a vehicle by id.
     */
    @PutMapping("/{id}")
    public VehicleResponse updateVehicle(@PathVariable String id, @Valid @RequestBody UpdateVehicleRequest request) {
        return vehicleService.update(id, request);
    }

    /**
     * Deletes a vehicle by id.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVehicle(@PathVariable String id) {
        vehicleService.delete(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Uploads images/documents for a vehicle and appends them to existing media.
     */
    @PostMapping("/{id}/media")
    public ResponseEntity<VehicleResponse> uploadVehicleMedia(
            @PathVariable String id,
            @RequestPart(value = "images", required = false) List<MultipartFile> images,
            @RequestPart(value = "files", required = false) List<MultipartFile> documents
    ) throws IOException {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(vehicleService.uploadMedia(id, images, documents));
    }
}
