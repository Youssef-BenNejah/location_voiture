package brama.pressing_api.review;

import brama.pressing_api.review.domain.model.Review;
import brama.pressing_api.review.dto.response.ReviewResponse;

public final class ReviewMapper {
    private ReviewMapper() {
    }

    public static ReviewResponse toResponse(final Review review) {
        return ReviewResponse.builder()
                .id(review.getId())
                .userId(review.getUserId())
                .vehicleId(review.getVehicleId())
                .rating(review.getRating())
                .comment(review.getComment())
                .status(review.getStatus())
                .createdDate(review.getCreatedDate())
                .lastModifiedDate(review.getLastModifiedDate())
                .build();
    }
}
