package brama.pressing_api.excursion;

import brama.pressing_api.excursion.domain.model.ExcursionDurationType;
import brama.pressing_api.excursion.dto.request.CreateExcursionRequest;
import brama.pressing_api.excursion.dto.request.UpdateExcursionRequest;
import brama.pressing_api.excursion.dto.response.ExcursionResponse;
import brama.pressing_api.excursion.service.ExcursionSearchCriteria;
import brama.pressing_api.excursion.service.ExcursionService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

/**
 * Admin endpoints to manage excursions.
 */
@RestController
@RequestMapping("/api/v1/admin/excursions")
@RequiredArgsConstructor
@Tag(name = "Excursions - Admin", description = "Admin excursion management")
@PreAuthorize("hasRole('ADMIN')")
public class ExcursionAdminController {
    private final ExcursionService excursionService;

    /**
     * Lists excursions with optional filters and pagination.
     */
    @GetMapping
    public Page<ExcursionResponse> listExcursions(@RequestParam(required = false) String q,
                                                  @RequestParam(required = false) String category,
                                                  @RequestParam(required = false) String durationType,
                                                  @RequestParam(required = false) BigDecimal minPrice,
                                                  @RequestParam(required = false) BigDecimal maxPrice,
                                                  @RequestParam(required = false) Boolean enabled,
                                                  Pageable pageable) {
        ExcursionSearchCriteria criteria = ExcursionSearchCriteria.builder()
                .query(q)
                .category(category)
                .durationType(parseDurationType(durationType))
                .minPrice(minPrice)
                .maxPrice(maxPrice)
                .enabledOnly(enabled)
                .build();
        return excursionService.listAdmin(criteria, pageable);
    }

    /**
     * Returns a single excursion by id.
     */
    @GetMapping("/{id}")
    public ExcursionResponse getExcursion(@PathVariable String id) {
        return excursionService.getById(id);
    }

    /**
     * Creates a new excursion.
     */
    @PostMapping
    public ResponseEntity<ExcursionResponse> createExcursion(@Valid @RequestBody CreateExcursionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(excursionService.create(request));
    }

    /**
     * Updates an excursion by id.
     */
    @PutMapping("/{id}")
    public ExcursionResponse updateExcursion(@PathVariable String id,
                                             @Valid @RequestBody UpdateExcursionRequest request) {
        return excursionService.update(id, request);
    }

    /**
     * Enables or disables an excursion.
     */
    @PatchMapping("/{id}/enabled")
    public ExcursionResponse setEnabled(@PathVariable String id, @RequestParam boolean enabled) {
        return excursionService.setEnabled(id, enabled);
    }

    /**
     * Deletes an excursion by id.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteExcursion(@PathVariable String id) {
        excursionService.delete(id);
        return ResponseEntity.noContent().build();
    }

    private ExcursionDurationType parseDurationType(final String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return ExcursionDurationType.valueOf(value.trim().toUpperCase());
    }

    @PostMapping("/{id}/images")
    public ResponseEntity<ExcursionResponse> uploadExcursionImages(
            @PathVariable String id,
            @RequestPart(value = "images") List<MultipartFile> images
    ) throws IOException {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(excursionService.uploadImages(id, images));
    }
}
