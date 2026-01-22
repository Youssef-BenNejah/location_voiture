package brama.pressing_api.promotion.domain.model;

import brama.pressing_api.common.BaseDocument;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.math.BigDecimal;
import java.time.LocalDate;

@Document(collection = "promotions")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class Promotion extends BaseDocument {
    @Field("code")
    @Indexed(unique = true)
    private String code;

    @Field("description")
    private String description;

    @Field("discount_type")
    private DiscountType discountType;

    @Field("value")
    private BigDecimal value;

    @Field("min_booking_amount")
    private BigDecimal minBookingAmount;

    @Field("max_discount_amount")
    private BigDecimal maxDiscountAmount;

    @Field("valid_from")
    private LocalDate validFrom;

    @Field("valid_until")
    private LocalDate validUntil;

    @Field("max_redemptions")
    private Integer maxRedemptions;

    @Field("usage_count")
    private Integer usageCount;

    @Field("active")
    private Boolean active;
}
