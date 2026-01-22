package brama.pressing_api.review.dto.request;

import brama.pressing_api.review.domain.model.ReviewStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateReviewStatusRequest {
    @NotNull
    private ReviewStatus status;
}
