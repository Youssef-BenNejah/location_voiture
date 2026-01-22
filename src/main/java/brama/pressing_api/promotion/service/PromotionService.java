package brama.pressing_api.promotion.service;

import brama.pressing_api.promotion.dto.request.CreatePromotionRequest;
import brama.pressing_api.promotion.dto.request.UpdatePromotionRequest;
import brama.pressing_api.promotion.dto.response.PromotionResponse;
import brama.pressing_api.promotion.dto.response.PromotionValidationResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;

public interface PromotionService {
    PromotionResponse create(CreatePromotionRequest request);

    PromotionResponse update(String promotionId, UpdatePromotionRequest request);

    PromotionResponse getById(String promotionId);

    void delete(String promotionId);

    Page<PromotionResponse> listAdmin(Pageable pageable);

    PromotionValidationResponse validateCode(String code, BigDecimal bookingAmount);

    BigDecimal calculateDiscount(String code, BigDecimal bookingAmount);
}
