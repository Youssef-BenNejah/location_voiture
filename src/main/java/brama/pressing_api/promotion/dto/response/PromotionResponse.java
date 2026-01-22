package brama.pressing_api.promotion.dto.response;

import brama.pressing_api.promotion.domain.model.DiscountType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PromotionResponse {
    private String id;
    private String code;
    private String description;
    private DiscountType discountType;
    private BigDecimal value;
    private BigDecimal minBookingAmount;
    private BigDecimal maxDiscountAmount;
    private LocalDate validFrom;
    private LocalDate validUntil;
    private Integer maxRedemptions;
    private Integer usageCount;
    private Boolean active;
    private LocalDateTime createdDate;
    private LocalDateTime lastModifiedDate;
}
