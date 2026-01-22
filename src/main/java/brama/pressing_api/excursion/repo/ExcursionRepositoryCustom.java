package brama.pressing_api.excursion.repo;

import brama.pressing_api.excursion.domain.model.Excursion;
import brama.pressing_api.excursion.service.ExcursionSearchCriteria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ExcursionRepositoryCustom {
    Page<Excursion> search(ExcursionSearchCriteria criteria, Pageable pageable);
}
