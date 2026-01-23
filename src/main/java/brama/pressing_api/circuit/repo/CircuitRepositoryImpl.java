package brama.pressing_api.circuit.repo;

import brama.pressing_api.circuit.domain.Circuit;
import brama.pressing_api.circuit.service.CircuitSearchCriteria;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@RequiredArgsConstructor
public class CircuitRepositoryImpl implements CircuitRepositoryCustom {
    private final MongoTemplate mongoTemplate;

    @Override
    public Page<Circuit> search(final CircuitSearchCriteria criteria, final Pageable pageable) {
        Query query = new Query();
        List<Criteria> filters = new ArrayList<>();

        if (criteria != null) {
            if (criteria.getQuery() != null && !criteria.getQuery().isBlank()) {
                String regex = ".*" + Pattern.quote(criteria.getQuery().trim()) + ".*";
                filters.add(new Criteria().orOperator(
                        Criteria.where("title").regex(regex, "i"),
                        Criteria.where("description").regex(regex, "i"),
                        Criteria.where("origin_city").regex(regex, "i"),
                        Criteria.where("destination_city").regex(regex, "i")
                ));
            }
            if (criteria.getOriginCity() != null && !criteria.getOriginCity().isBlank()) {
                filters.add(Criteria.where("origin_city").is(criteria.getOriginCity()));
            }
            if (criteria.getDestinationCity() != null && !criteria.getDestinationCity().isBlank()) {
                filters.add(Criteria.where("destination_city").is(criteria.getDestinationCity()));
            }
            if (criteria.getVehicleType() != null) {
                filters.add(Criteria.where("vehicle_type").is(criteria.getVehicleType()));
            }
            if (criteria.getMinPrice() != null) {
                filters.add(Criteria.where("price").gte(criteria.getMinPrice()));
            }
            if (criteria.getMaxPrice() != null) {
                filters.add(Criteria.where("price").lte(criteria.getMaxPrice()));
            }
            if (criteria.getStatus() != null) {
                filters.add(Criteria.where("status").is(criteria.getStatus()));
            }
        }

        if (!filters.isEmpty()) {
            query.addCriteria(new Criteria().andOperator(filters.toArray(new Criteria[0])));
        }

        long total = mongoTemplate.count(query, Circuit.class);
        List<Circuit> results = mongoTemplate.find(query.with(pageable), Circuit.class);
        return new PageImpl<>(results, pageable, total);
    }
}
