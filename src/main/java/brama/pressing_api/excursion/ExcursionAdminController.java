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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

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
}
