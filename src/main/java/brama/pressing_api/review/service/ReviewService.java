package brama.pressing_api.review.service;

import brama.pressing_api.review.domain.model.ReviewStatus;
import brama.pressing_api.review.dto.request.CreateReviewRequest;
import brama.pressing_api.review.dto.response.ReviewResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ReviewService {
    ReviewResponse create(CreateReviewRequest request);

    List<ReviewResponse> listApprovedByVehicle(String vehicleId);

    Page<ReviewResponse> listAdmin(Pageable pageable);

    ReviewResponse updateStatus(String reviewId, ReviewStatus status);
}
