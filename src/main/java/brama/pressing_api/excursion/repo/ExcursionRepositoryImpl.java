package brama.pressing_api.excursion.repo;

import brama.pressing_api.excursion.domain.model.Excursion;
import brama.pressing_api.excursion.service.ExcursionSearchCriteria;
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
public class ExcursionRepositoryImpl implements ExcursionRepositoryCustom {
    private final MongoTemplate mongoTemplate;

    @Override
    public Page<Excursion> search(final ExcursionSearchCriteria criteria, final Pageable pageable) {
        Query query = new Query();
        List<Criteria> filters = new ArrayList<>();

        if (criteria.getQuery() != null && !criteria.getQuery().isBlank()) {
            String regex = ".*" + Pattern.quote(criteria.getQuery().trim()) + ".*";
            filters.add(new Criteria().orOperator(
                    Criteria.where("title").regex(regex, "i"),
                    Criteria.where("description").regex(regex, "i"),
                    Criteria.where("start_location").regex(regex, "i"),
                    Criteria.where("end_location").regex(regex, "i")
            ));
        }
        if (criteria.getCategory() != null && !criteria.getCategory().isBlank()) {
            filters.add(Criteria.where("category").is(criteria.getCategory()));
        }
        if (criteria.getDurationType() != null) {
            filters.add(Criteria.where("duration_type").is(criteria.getDurationType()));
        }
        if (criteria.getMinPrice() != null) {
            filters.add(Criteria.where("price_per_person").gte(criteria.getMinPrice()));
        }
        if (criteria.getMaxPrice() != null) {
            filters.add(Criteria.where("price_per_person").lte(criteria.getMaxPrice()));
        }
        if (criteria.getEnabledOnly() != null) {
            if (criteria.getEnabledOnly()) {
                filters.add(Criteria.where("is_enabled").is(Boolean.TRUE));
            } else {
                filters.add(Criteria.where("is_enabled").is(Boolean.FALSE));
            }
        }
        if (criteria.getAvailableDate() != null) {
            filters.add(Criteria.where("available_dates").is(criteria.getAvailableDate()));
        }

        if (!filters.isEmpty()) {
            query.addCriteria(new Criteria().andOperator(filters.toArray(new Criteria[0])));
        }

        long total = mongoTemplate.count(query, Excursion.class);
        List<Excursion> results = mongoTemplate.find(query.with(pageable), Excursion.class);
        return new PageImpl<>(results, pageable, total);
    }
}
