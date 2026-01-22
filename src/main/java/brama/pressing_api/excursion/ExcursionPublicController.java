package brama.pressing_api.excursion;

import brama.pressing_api.excursion.domain.model.ExcursionDurationType;
import brama.pressing_api.excursion.dto.response.ExcursionResponse;
import brama.pressing_api.excursion.service.ExcursionSearchCriteria;
import brama.pressing_api.excursion.service.ExcursionService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Public endpoints to browse and view enabled excursions.
 */
@RestController
@RequestMapping("/api/v1/public/excursions")
@RequiredArgsConstructor
@Tag(name = "Excursions - Public", description = "Public excursion catalog")
public class ExcursionPublicController {
    private final ExcursionService excursionService;

    /**
     * Lists enabled excursions with optional filters for search, category, duration type, price, and date.
     */
    @GetMapping
    public List<ExcursionResponse> listExcursions(@RequestParam(required = false) String q,
                                                  @RequestParam(required = false) String category,
                                                  @RequestParam(required = false) String durationType,
                                                  @RequestParam(required = false) BigDecimal minPrice,
                                                  @RequestParam(required = false) BigDecimal maxPrice,
                                                  @RequestParam(required = false)
                                                  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
                                                  LocalDate date) {
        ExcursionSearchCriteria criteria = ExcursionSearchCriteria.builder()
                .query(q)
                .category(category)
                .durationType(parseDurationType(durationType))
                .minPrice(minPrice)
                .maxPrice(maxPrice)
                .availableDate(date)
                .build();
        return excursionService.listPublic(criteria);
    }

    /**
     * Returns details for a single enabled excursion.
     */
    @GetMapping("/{id}")
    public ExcursionResponse getExcursion(@PathVariable String id) {
        return excursionService.getPublicById(id);
    }

    private ExcursionDurationType parseDurationType(final String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return ExcursionDurationType.valueOf(value.trim().toUpperCase());
    }
}
