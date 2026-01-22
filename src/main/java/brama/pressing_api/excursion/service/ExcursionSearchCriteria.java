package brama.pressing_api.excursion.service;

import brama.pressing_api.excursion.domain.model.ExcursionDurationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExcursionSearchCriteria {
    private String query;
    private String category;
    private ExcursionDurationType durationType;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    private Boolean enabledOnly;
    private LocalDate availableDate;

    public String cacheKey() {
        return String.join("|",
                safe(query),
                safe(category),
                durationType != null ? durationType.name() : "",
                minPrice != null ? minPrice.toPlainString() : "",
                maxPrice != null ? maxPrice.toPlainString() : "",
                enabledOnly != null ? enabledOnly.toString() : "",
                availableDate != null ? availableDate.toString() : ""
        );
    }

    private String safe(final String value) {
        return value == null ? "" : value.trim().toLowerCase();
    }
}
