package brama.pressing_api.review;

import brama.pressing_api.review.dto.request.UpdateReviewStatusRequest;
import brama.pressing_api.review.dto.response.ReviewResponse;
import brama.pressing_api.review.service.ReviewService;
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
import org.springframework.web.bind.annotation.RestController;

/**
 * Admin endpoints to moderate reviews.
 */
@RestController
@RequestMapping("/api/v1/admin/reviews")
@RequiredArgsConstructor
@Tag(name = "Reviews - Admin", description = "Admin review moderation")
@PreAuthorize("hasRole('ADMIN')")
public class ReviewAdminController {
    private final ReviewService reviewService;

    /**
     * Lists reviews with pagination.
     */
    @GetMapping
    public Page<ReviewResponse> listReviews(Pageable pageable) {
        return reviewService.listAdmin(pageable);
    }

    /**
     * Updates review moderation status.
     */
    @PatchMapping("/{id}/status")
    public ReviewResponse updateStatus(@PathVariable String id,
                                       @Valid @RequestBody UpdateReviewStatusRequest request) {
        return reviewService.updateStatus(id, request.getStatus());
    }
}
