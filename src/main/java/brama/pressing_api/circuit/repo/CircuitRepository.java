package brama.pressing_api.circuit.repo;

import brama.pressing_api.circuit.domain.Circuit;
import brama.pressing_api.circuit.domain.CircuitStatus;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface CircuitRepository extends MongoRepository<Circuit, String>, CircuitRepositoryCustom {
    List<Circuit> findByStatus(CircuitStatus status);
}
