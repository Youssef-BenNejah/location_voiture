package brama.pressing_api.vehicle.repo;

import brama.pressing_api.vehicle.domain.model.Vehicle;
import brama.pressing_api.vehicle.service.VehicleSearchCriteria;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class VehicleRepositoryImpl implements VehicleRepositoryCustom {
    private final MongoTemplate mongoTemplate;

    @Override
    public Page<Vehicle> search(final VehicleSearchCriteria criteria, final Pageable pageable) {
        Query query = new Query();
        List<Criteria> filters = new ArrayList<>();

        if (criteria.getLocationId() != null && !criteria.getLocationId().isBlank()) {
            filters.add(Criteria.where("location_id").is(criteria.getLocationId()));
        }
        if (criteria.getCategory() != null) {
            filters.add(Criteria.where("category").is(criteria.getCategory()));
        }
        if (criteria.getTransmission() != null) {
            filters.add(Criteria.where("transmission").is(criteria.getTransmission()));
        }
        if (criteria.getFuelType() != null) {
            filters.add(Criteria.where("fuel_type").is(criteria.getFuelType()));
        }
        if (criteria.getMinSeats() != null) {
            filters.add(Criteria.where("seats").gte(criteria.getMinSeats()));
        }
        if (criteria.getMinPrice() != null) {
            filters.add(Criteria.where("daily_rate").gte(criteria.getMinPrice()));
        }
        if (criteria.getMaxPrice() != null) {
            filters.add(Criteria.where("daily_rate").lte(criteria.getMaxPrice()));
        }
        if (criteria.getStatuses() != null && !criteria.getStatuses().isEmpty()) {
            filters.add(Criteria.where("status").in(criteria.getStatuses()));
        }
        if (criteria.getExcludeVehicleIds() != null && !criteria.getExcludeVehicleIds().isEmpty()) {
            filters.add(Criteria.where("_id").nin(criteria.getExcludeVehicleIds()));
        }

        if (!filters.isEmpty()) {
            query.addCriteria(new Criteria().andOperator(filters.toArray(new Criteria[0])));
        }

        long total = mongoTemplate.count(query, Vehicle.class);
        List<Vehicle> results = mongoTemplate.find(query.with(pageable), Vehicle.class);
        return new PageImpl<>(results, pageable, total);
    }
}
