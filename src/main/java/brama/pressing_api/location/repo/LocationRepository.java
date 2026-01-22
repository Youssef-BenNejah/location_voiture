package brama.pressing_api.location.repo;

import brama.pressing_api.location.domain.model.Location;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface LocationRepository extends MongoRepository<Location, String> {
    boolean existsByCodeIgnoreCase(String code);
}
