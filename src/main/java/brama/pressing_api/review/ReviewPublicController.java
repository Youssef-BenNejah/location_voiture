package brama.pressing_api.review;

import brama.pressing_api.review.dto.response.ReviewResponse;
import brama.pressing_api.review.service.ReviewService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Public review listing endpoint.
 */
@RestController
@RequestMapping("/api/v1/public/reviews")
@RequiredArgsConstructor
@Tag(name = "Reviews - Public", description = "Public review listing")
public class ReviewPublicController {
    private final ReviewService reviewService;

    /**
     * Lists approved reviews for a vehicle.
     */
    @GetMapping
    public List<ReviewResponse> listApproved(@RequestParam String vehicleId) {
        return reviewService.listApprovedByVehicle(vehicleId);
    }
}
