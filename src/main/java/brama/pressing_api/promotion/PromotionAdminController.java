package brama.pressing_api.promotion;

import brama.pressing_api.promotion.dto.request.CreatePromotionRequest;
import brama.pressing_api.promotion.dto.request.UpdatePromotionRequest;
import brama.pressing_api.promotion.dto.response.PromotionResponse;
import brama.pressing_api.promotion.service.PromotionService;
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
 * Admin endpoints to manage promotions.
 */
@RestController
@RequestMapping("/api/v1/admin/promotions")
@RequiredArgsConstructor
@Tag(name = "Promotions - Admin", description = "Admin promotion management")
@PreAuthorize("hasRole('ADMIN')")
public class PromotionAdminController {
    private final PromotionService promotionService;

    /**
     * Lists promotions with pagination.
     */
    @GetMapping
    public Page<PromotionResponse> listPromotions(Pageable pageable) {
        return promotionService.listAdmin(pageable);
    }

    /**
     * Returns a promotion by id.
     */
    @GetMapping("/{id}")
    public PromotionResponse getPromotion(@PathVariable String id) {
        return promotionService.getById(id);
    }

    /**
     * Creates a new promotion.
     */
    @PostMapping
    public ResponseEntity<PromotionResponse> createPromotion(@Valid @RequestBody CreatePromotionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(promotionService.create(request));
    }

    /**
     * Updates a promotion by id.
     */
    @PutMapping("/{id}")
    public PromotionResponse updatePromotion(@PathVariable String id,
                                             @Valid @RequestBody UpdatePromotionRequest request) {
        return promotionService.update(id, request);
    }

    /**
     * Deletes a promotion by id.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePromotion(@PathVariable String id) {
        promotionService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
