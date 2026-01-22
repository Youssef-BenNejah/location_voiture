package brama.pressing_api.vehicle.repo;

import brama.pressing_api.vehicle.domain.model.Vehicle;
import brama.pressing_api.vehicle.domain.model.VehicleStatus;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface VehicleRepository extends MongoRepository<Vehicle, String>, VehicleRepositoryCustom {
    long countByStatus(VehicleStatus status);
}
