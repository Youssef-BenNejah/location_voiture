package brama.pressing_api.excursionbooking.repo;

import brama.pressing_api.excursionbooking.domain.model.ExcursionBooking;
import brama.pressing_api.excursionbooking.service.ExcursionBookingSearchCriteria;
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
public class ExcursionBookingRepositoryImpl implements ExcursionBookingRepositoryCustom {
    private final MongoTemplate mongoTemplate;

    @Override
    public Page<ExcursionBooking> search(final ExcursionBookingSearchCriteria criteria, final Pageable pageable) {
        Query query = new Query();
        List<Criteria> filters = new ArrayList<>();

        if (criteria.getQuery() != null && !criteria.getQuery().isBlank()) {
            String regex = ".*" + Pattern.quote(criteria.getQuery().trim()) + ".*";
            filters.add(new Criteria().orOperator(
                    Criteria.where("customer_name").regex(regex, "i"),
                    Criteria.where("customer_email").regex(regex, "i"),
                    Criteria.where("customer_phone").regex(regex, "i"),
                    Criteria.where("excursion_title").regex(regex, "i")
            ));
        }
        if (criteria.getStatus() != null) {
            filters.add(Criteria.where("status").is(criteria.getStatus()));
        }
        if (criteria.getExcursionId() != null && !criteria.getExcursionId().isBlank()) {
            filters.add(Criteria.where("excursion_id").is(criteria.getExcursionId()));
        }
        if (criteria.getSelectedDate() != null) {
            filters.add(Criteria.where("selected_date").is(criteria.getSelectedDate()));
        }

        if (!filters.isEmpty()) {
            query.addCriteria(new Criteria().andOperator(filters.toArray(new Criteria[0])));
        }

        long total = mongoTemplate.count(query, ExcursionBooking.class);
        List<ExcursionBooking> results = mongoTemplate.find(query.with(pageable), ExcursionBooking.class);
        return new PageImpl<>(results, pageable, total);
    }
}
