package brama.pressing_api.vehicle.repo;

import brama.pressing_api.vehicle.domain.model.Vehicle;
import brama.pressing_api.vehicle.service.VehicleSearchCriteria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface VehicleRepositoryCustom {
    Page<Vehicle> search(VehicleSearchCriteria criteria, Pageable pageable);
}
