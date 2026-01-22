package brama.pressing_api.review.dto.response;

import brama.pressing_api.review.domain.model.ReviewStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewResponse {
    private String id;
    private String userId;
    private String vehicleId;
    private Integer rating;
    private String comment;
    private ReviewStatus status;
    private LocalDateTime createdDate;
    private LocalDateTime lastModifiedDate;
}
