package brama.pressing_api.location.service;

import brama.pressing_api.location.dto.request.CreateLocationRequest;
import brama.pressing_api.location.dto.request.UpdateLocationRequest;
import brama.pressing_api.location.dto.response.LocationResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface LocationService {
    LocationResponse create(CreateLocationRequest request);

    LocationResponse update(String locationId, UpdateLocationRequest request);

    LocationResponse getById(String locationId);

    void delete(String locationId);

    List<LocationResponse> listPublic();

    Page<LocationResponse> listAdmin(Pageable pageable);
}
