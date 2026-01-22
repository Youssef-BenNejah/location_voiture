package brama.pressing_api.promotion.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PromotionValidationResponse {
    private String code;
    private boolean valid;
    private BigDecimal discountAmount;
    private String message;
}
