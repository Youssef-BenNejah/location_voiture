package brama.pressing_api.circuit.repo;

import brama.pressing_api.circuit.domain.CircuitBooking;
import brama.pressing_api.circuit.service.CircuitBookingSearchCriteria;
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
public class CircuitBookingRepositoryImpl implements CircuitBookingRepositoryCustom {
    private final MongoTemplate mongoTemplate;

    @Override
    public Page<CircuitBooking> search(final CircuitBookingSearchCriteria criteria, final Pageable pageable) {
        Query query = new Query();
        List<Criteria> filters = new ArrayList<>();

        if (criteria != null) {
            if (criteria.getQuery() != null && !criteria.getQuery().isBlank()) {
                String regex = ".*" + Pattern.quote(criteria.getQuery().trim()) + ".*";
                filters.add(new Criteria().orOperator(
                        Criteria.where("customer_name").regex(regex, "i"),
                        Criteria.where("customer_email").regex(regex, "i"),
                        Criteria.where("customer_phone").regex(regex, "i"),
                        Criteria.where("circuit_title").regex(regex, "i")
                ));
            }
            if (criteria.getStatus() != null) {
                filters.add(Criteria.where("status").is(criteria.getStatus()));
            }
            if (criteria.getCircuitId() != null && !criteria.getCircuitId().isBlank()) {
                filters.add(Criteria.where("circuit_id").is(criteria.getCircuitId()));
            }
            if (criteria.getSelectedDate() != null) {
                filters.add(Criteria.where("selected_date").is(criteria.getSelectedDate()));
            }
        }

        if (!filters.isEmpty()) {
            query.addCriteria(new Criteria().andOperator(filters.toArray(new Criteria[0])));
        }

        long total = mongoTemplate.count(query, CircuitBooking.class);
        List<CircuitBooking> results = mongoTemplate.find(query.with(pageable), CircuitBooking.class);
        return new PageImpl<>(results, pageable, total);
    }
}
