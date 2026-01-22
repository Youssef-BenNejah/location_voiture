package brama.pressing_api.promotion.dto.request;

import brama.pressing_api.promotion.domain.model.DiscountType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
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
public class CreatePromotionRequest {
    @NotBlank
    private String code;

    private String description;

    @NotNull
    private DiscountType discountType;

    @NotNull
    @Positive
    private BigDecimal value;

    @PositiveOrZero
    private BigDecimal minBookingAmount;

    @PositiveOrZero
    private BigDecimal maxDiscountAmount;

    private LocalDate validFrom;
    private LocalDate validUntil;

    @PositiveOrZero
    private Integer maxRedemptions;

    private Boolean active;
}
