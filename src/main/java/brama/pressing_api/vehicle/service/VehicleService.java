package brama.pressing_api.vehicle.service;

import brama.pressing_api.vehicle.dto.request.CreateVehicleRequest;
import brama.pressing_api.vehicle.dto.request.UpdateVehicleRequest;
import brama.pressing_api.vehicle.dto.response.VehicleResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface VehicleService {
    VehicleResponse create(CreateVehicleRequest request);

    VehicleResponse update(String vehicleId, UpdateVehicleRequest request);

    VehicleResponse getById(String vehicleId);

    void delete(String vehicleId);

    Page<VehicleResponse> searchPublic(VehicleSearchCriteria criteria, Pageable pageable);

    Page<VehicleResponse> listAdmin(VehicleSearchCriteria criteria, Pageable pageable);

    VehicleResponse uploadMedia(String vehicleId, List<MultipartFile> images, List<MultipartFile> documents)
            throws IOException;
}
