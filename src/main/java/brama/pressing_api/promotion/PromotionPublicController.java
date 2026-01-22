package brama.pressing_api.promotion;

import brama.pressing_api.promotion.dto.response.PromotionValidationResponse;
import brama.pressing_api.promotion.service.PromotionService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

/**
 * Public promotion validation endpoint.
 */
@RestController
@RequestMapping("/api/v1/public/promotions")
@RequiredArgsConstructor
@Tag(name = "Promotions - Public", description = "Public promotion validation")
public class PromotionPublicController {
    private final PromotionService promotionService;

    /**
     * Validates a promo code against an optional booking amount.
     */
    @GetMapping("/validate")
    public PromotionValidationResponse validate(@RequestParam String code,
                                                @RequestParam(required = false) BigDecimal amount) {
        return promotionService.validateCode(code, amount);
    }
}
