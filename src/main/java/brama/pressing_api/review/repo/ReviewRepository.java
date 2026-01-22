package brama.pressing_api.review.repo;

import brama.pressing_api.review.domain.model.Review;
import brama.pressing_api.review.domain.model.ReviewStatus;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ReviewRepository extends MongoRepository<Review, String> {
    List<Review> findByVehicleIdAndStatus(String vehicleId, ReviewStatus status);
}
