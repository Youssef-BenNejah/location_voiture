package brama.pressing_api.promotion.repo;

import brama.pressing_api.promotion.domain.model.Promotion;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface PromotionRepository extends MongoRepository<Promotion, String> {
    Optional<Promotion> findByCodeIgnoreCase(String code);
    boolean existsByCodeIgnoreCase(String code);
}
