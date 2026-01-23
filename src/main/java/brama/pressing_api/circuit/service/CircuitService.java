package brama.pressing_api.circuit.service;

import brama.pressing_api.circuit.dto.request.CircuitRequest;
import brama.pressing_api.circuit.dto.response.CircuitAdminStatsResponse;
import brama.pressing_api.circuit.dto.response.CircuitResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CircuitService {
    Page<CircuitResponse> listPublic(CircuitSearchCriteria criteria, Pageable pageable);

    CircuitResponse getPublic(String id);

    Page<CircuitResponse> listAdmin(CircuitSearchCriteria criteria, Pageable pageable);

    CircuitResponse create(CircuitRequest request);

    CircuitResponse update(String id, CircuitRequest request);

    void delete(String id);

    CircuitAdminStatsResponse getAdminStats();
}
