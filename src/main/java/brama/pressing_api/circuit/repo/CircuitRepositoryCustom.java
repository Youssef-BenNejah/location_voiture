package brama.pressing_api.circuit.repo;

import brama.pressing_api.circuit.domain.Circuit;
import brama.pressing_api.circuit.service.CircuitSearchCriteria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CircuitRepositoryCustom {
    Page<Circuit> search(CircuitSearchCriteria criteria, Pageable pageable);
}
