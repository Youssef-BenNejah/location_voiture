package brama.pressing_api.promotion;

import brama.pressing_api.promotion.domain.model.Promotion;
import brama.pressing_api.promotion.dto.request.CreatePromotionRequest;
import brama.pressing_api.promotion.dto.request.UpdatePromotionRequest;
import brama.pressing_api.promotion.dto.response.PromotionResponse;

public final class PromotionMapper {
    private PromotionMapper() {
    }

    public static Promotion toEntity(final CreatePromotionRequest request) {
        return Promotion.builder()
                .code(request.getCode())
                .description(request.getDescription())
                .discountType(request.getDiscountType())
                .value(request.getValue())
                .minBookingAmount(request.getMinBookingAmount())
                .maxDiscountAmount(request.getMaxDiscountAmount())
                .validFrom(request.getValidFrom())
                .validUntil(request.getValidUntil())
                .maxRedemptions(request.getMaxRedemptions())
                .usageCount(0)
                .active(request.getActive() != null ? request.getActive() : Boolean.TRUE)
                .build();
    }

    public static void applyUpdates(final Promotion promotion, final UpdatePromotionRequest request) {
        if (request.getCode() != null) {
            promotion.setCode(request.getCode());
        }
        if (request.getDescription() != null) {
            promotion.setDescription(request.getDescription());
        }
        if (request.getDiscountType() != null) {
            promotion.setDiscountType(request.getDiscountType());
        }
        if (request.getValue() != null) {
            promotion.setValue(request.getValue());
        }
        if (request.getMinBookingAmount() != null) {
            promotion.setMinBookingAmount(request.getMinBookingAmount());
        }
        if (request.getMaxDiscountAmount() != null) {
            promotion.setMaxDiscountAmount(request.getMaxDiscountAmount());
        }
        if (request.getValidFrom() != null) {
            promotion.setValidFrom(request.getValidFrom());
        }
        if (request.getValidUntil() != null) {
            promotion.setValidUntil(request.getValidUntil());
        }
        if (request.getMaxRedemptions() != null) {
            promotion.setMaxRedemptions(request.getMaxRedemptions());
        }
        if (request.getActive() != null) {
            promotion.setActive(request.getActive());
        }
    }

    public static PromotionResponse toResponse(final Promotion promotion) {
        return PromotionResponse.builder()
                .id(promotion.getId())
                .code(promotion.getCode())
                .description(promotion.getDescription())
                .discountType(promotion.getDiscountType())
                .value(promotion.getValue())
                .minBookingAmount(promotion.getMinBookingAmount())
                .maxDiscountAmount(promotion.getMaxDiscountAmount())
                .validFrom(promotion.getValidFrom())
                .validUntil(promotion.getValidUntil())
                .maxRedemptions(promotion.getMaxRedemptions())
                .usageCount(promotion.getUsageCount())
                .active(promotion.getActive())
                .createdDate(promotion.getCreatedDate())
                .lastModifiedDate(promotion.getLastModifiedDate())
                .build();
    }
}
