package brama.pressing_api.vehicle.service;

import brama.pressing_api.vehicle.dto.request.CreateVehicleRequest;
import brama.pressing_api.vehicle.dto.request.UpdateVehicleRequest;
import brama.pressing_api.vehicle.dto.response.VehicleResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface VehicleService {
    VehicleResponse create(CreateVehicleRequest request);

    VehicleResponse update(String vehicleId, UpdateVehicleRequest request);

    VehicleResponse getById(String vehicleId);

    void delete(String vehicleId);

    Page<VehicleResponse> searchPublic(VehicleSearchCriteria criteria, Pageable pageable);

    Page<VehicleResponse> listAdmin(Pageable pageable);
}
