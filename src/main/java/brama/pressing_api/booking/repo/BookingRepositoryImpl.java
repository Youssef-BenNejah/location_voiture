package brama.pressing_api.booking.repo;

import brama.pressing_api.booking.domain.model.Booking;
import brama.pressing_api.booking.service.BookingSearchCriteria;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@RequiredArgsConstructor
public class BookingRepositoryImpl implements BookingRepositoryCustom {
    private final MongoTemplate mongoTemplate;

    @Override
    public Page<Booking> search(final BookingSearchCriteria criteria, final Pageable pageable) {
        Query query = new Query();
        List<Criteria> filters = new ArrayList<>();

        if (criteria != null) {
            if (criteria.getQuery() != null && !criteria.getQuery().isBlank()) {
                String regex = ".*" + Pattern.quote(criteria.getQuery().trim()) + ".*";
                filters.add(new Criteria().orOperator(
                        Criteria.where("_id").regex(regex, "i"),
                        Criteria.where("customer_name").regex(regex, "i"),
                        Criteria.where("customer_email").regex(regex, "i"),
                        Criteria.where("customer_phone").regex(regex, "i"),
                        Criteria.where("vehicle_name").regex(regex, "i"),
                        Criteria.where("notes").regex(regex, "i"),
                        Criteria.where("promo_code").regex(regex, "i")
                ));
            }
            if (criteria.getStatus() != null) {
                filters.add(Criteria.where("status").is(criteria.getStatus()));
            }
            if (criteria.getPaymentStatus() != null) {
                filters.add(Criteria.where("payment_status").is(criteria.getPaymentStatus()));
            }
            LocalDate startDate = criteria.getStartDate();
            LocalDate endDate = criteria.getEndDate();
            if (startDate != null) {
                filters.add(Criteria.where("start_date").gte(startDate));
            }
            if (endDate != null) {
                filters.add(Criteria.where("end_date").lte(endDate));
            }
        }

        if (!filters.isEmpty()) {
            query.addCriteria(new Criteria().andOperator(filters.toArray(new Criteria[0])));
        }

        long total = mongoTemplate.count(query, Booking.class);
        List<Booking> results = mongoTemplate.find(query.with(pageable), Booking.class);
        return new PageImpl<>(results, pageable, total);
    }
}
