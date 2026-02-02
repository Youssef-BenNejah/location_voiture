package brama.pressing_api.seed.location_seed.repository;

import brama.pressing_api.seed.location_seed.domain.LocationSeed;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface LocationSeedRepository extends MongoRepository<LocationSeed, String> {
    /**
     * Find all active locations
     */
    List<LocationSeed> findByActiveTrue();

    /**
     * Find locations by city
     */
    List<LocationSeed> findByCity(String city);

    /**
     * Find locations by country
     */
    List<LocationSeed> findByCountry(String country);
}
