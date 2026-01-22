package brama.pressing_api.review.service.impl;

import brama.pressing_api.exception.BusinessException;
import brama.pressing_api.exception.EntityNotFoundException;
import brama.pressing_api.exception.ErrorCode;
import brama.pressing_api.review.ReviewMapper;
import brama.pressing_api.review.domain.model.Review;
import brama.pressing_api.review.domain.model.ReviewStatus;
import brama.pressing_api.review.dto.request.CreateReviewRequest;
import brama.pressing_api.review.dto.response.ReviewResponse;
import brama.pressing_api.review.repo.ReviewRepository;
import brama.pressing_api.review.service.ReviewService;
import brama.pressing_api.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {
    private final ReviewRepository reviewRepository;

    @Override
    public ReviewResponse create(final CreateReviewRequest request) {
        String userId = SecurityUtils.getCurrentUserId()
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        Review review = Review.builder()
                .userId(userId)
                .vehicleId(request.getVehicleId())
                .rating(request.getRating())
                .comment(request.getComment())
                .status(ReviewStatus.PENDING)
                .build();
        return ReviewMapper.toResponse(reviewRepository.save(review));
    }

    @Override
    public List<ReviewResponse> listApprovedByVehicle(final String vehicleId) {
        return reviewRepository.findByVehicleIdAndStatus(vehicleId, ReviewStatus.APPROVED)
                .stream()
                .map(ReviewMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public Page<ReviewResponse> listAdmin(final Pageable pageable) {
        return reviewRepository.findAll(pageable).map(ReviewMapper::toResponse);
    }

    @Override
    public ReviewResponse updateStatus(final String reviewId, final ReviewStatus status) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new EntityNotFoundException("Review not found"));
        review.setStatus(status);
        return ReviewMapper.toResponse(reviewRepository.save(review));
    }
}
