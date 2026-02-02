package brama.pressing_api.seed.extra.repository;

import brama.pressing_api.seed.extra.domain.Extra;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ExtraRepository extends MongoRepository<Extra, String> {
    /**
     * Find all active extras
     */
    List<Extra> findByActiveTrue();

    /**
     * Find extras by category
     */
    List<Extra> findByCategory(String category);

    /**
     * Find extras by category and active status
     */
    List<Extra> findByCategoryAndActiveTrue(String category);
}

