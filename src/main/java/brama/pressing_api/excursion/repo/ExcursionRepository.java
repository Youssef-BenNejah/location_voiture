package brama.pressing_api.excursion.repo;

import brama.pressing_api.excursion.domain.model.Excursion;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface ExcursionRepository extends MongoRepository<Excursion, String>, ExcursionRepositoryCustom {
    Optional<Excursion> findByIdAndIsEnabledTrue(String id);
}
