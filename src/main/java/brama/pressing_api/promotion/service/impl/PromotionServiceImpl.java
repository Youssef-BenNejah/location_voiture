package brama.pressing_api.promotion.service.impl;

import brama.pressing_api.exception.BusinessException;
import brama.pressing_api.exception.EntityNotFoundException;
import brama.pressing_api.exception.ErrorCode;
import brama.pressing_api.promotion.PromotionMapper;
import brama.pressing_api.promotion.domain.model.DiscountType;
import brama.pressing_api.promotion.domain.model.Promotion;
import brama.pressing_api.promotion.dto.request.CreatePromotionRequest;
import brama.pressing_api.promotion.dto.request.UpdatePromotionRequest;
import brama.pressing_api.promotion.dto.response.PromotionResponse;
import brama.pressing_api.promotion.dto.response.PromotionValidationResponse;
import brama.pressing_api.promotion.repo.PromotionRepository;
import brama.pressing_api.promotion.service.PromotionService;
import brama.pressing_api.notification.domain.NotificationImportance;
import brama.pressing_api.notification.dto.NotificationRequest;
import brama.pressing_api.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class PromotionServiceImpl implements PromotionService {
    private final PromotionRepository promotionRepository;
    private final NotificationService notificationService;

    @Override
    public PromotionResponse create(final CreatePromotionRequest request) {
        if (promotionRepository.existsByCodeIgnoreCase(request.getCode())) {
            throw new BusinessException(ErrorCode.PROMO_CODE_EXISTS);
        }
        Promotion promotion = PromotionMapper.toEntity(request);
        Promotion saved = promotionRepository.save(promotion);
        notificationService.notifyAdmins(NotificationRequest.builder()
                .type("PROMOTION_CREATED")
                .title("Promotion created")
                .body("Promotion " + saved.getCode() + " has been created")
                .importance(NotificationImportance.LOW)
                .data(java.util.Map.of("promotionId", saved.getId(), "code", saved.getCode()))
                .build());
        return PromotionMapper.toResponse(saved);
    }

    @Override
    public PromotionResponse update(final String promotionId, final UpdatePromotionRequest request) {
        Promotion promotion = promotionRepository.findById(promotionId)
                .orElseThrow(() -> new EntityNotFoundException("Promotion not found"));
        if (request.getCode() != null && !request.getCode().equalsIgnoreCase(promotion.getCode())) {
            if (promotionRepository.existsByCodeIgnoreCase(request.getCode())) {
                throw new BusinessException(ErrorCode.PROMO_CODE_EXISTS);
            }
        }
        PromotionMapper.applyUpdates(promotion, request);
        Promotion saved = promotionRepository.save(promotion);
        notificationService.notifyAdmins(NotificationRequest.builder()
                .type("PROMOTION_UPDATED")
                .title("Promotion updated")
                .body("Promotion " + saved.getCode() + " has been updated")
                .importance(NotificationImportance.LOW)
                .data(java.util.Map.of("promotionId", saved.getId(), "code", saved.getCode()))
                .build());
        return PromotionMapper.toResponse(saved);
    }

    @Override
    public PromotionResponse getById(final String promotionId) {
        Promotion promotion = promotionRepository.findById(promotionId)
                .orElseThrow(() -> new EntityNotFoundException("Promotion not found"));
        return PromotionMapper.toResponse(promotion);
    }

    @Override
    public void delete(final String promotionId) {
        Promotion existing = promotionRepository.findById(promotionId)
                .orElseThrow(() -> new EntityNotFoundException("Promotion not found"));
        promotionRepository.deleteById(promotionId);
        notificationService.notifyAdmins(NotificationRequest.builder()
                .type("PROMOTION_DELETED")
                .title("Promotion deleted")
                .body("Promotion " + existing.getCode() + " has been deleted")
                .importance(NotificationImportance.LOW)
                .data(java.util.Map.of("promotionId", existing.getId(), "code", existing.getCode()))
                .build());
    }

    @Override
    public Page<PromotionResponse> listAdmin(final Pageable pageable) {
        return promotionRepository.findAll(pageable).map(PromotionMapper::toResponse);
    }

    @Override
    public PromotionValidationResponse validateCode(final String code, final BigDecimal bookingAmount) {
        if (code == null || code.isBlank()) {
            return PromotionValidationResponse.builder()
                    .code(code)
                    .valid(false)
                    .discountAmount(BigDecimal.ZERO)
                    .message("Promo code is required")
                    .build();
        }
        Promotion promotion = promotionRepository.findByCodeIgnoreCase(code)
                .orElse(null);
        if (promotion == null || !isValidPromotion(promotion, bookingAmount)) {
            return PromotionValidationResponse.builder()
                    .code(code)
                    .valid(false)
                    .discountAmount(BigDecimal.ZERO)
                    .message("Promo code is invalid or expired")
                    .build();
        }
        BigDecimal discount = calculateDiscountAmount(promotion, bookingAmount);
        return PromotionValidationResponse.builder()
                .code(code)
                .valid(true)
                .discountAmount(discount)
                .message("Promo code is valid")
                .build();
    }

    @Override
    public BigDecimal calculateDiscount(final String code, final BigDecimal bookingAmount) {
        if (code == null || code.isBlank()) {
            return BigDecimal.ZERO;
        }
        Promotion promotion = promotionRepository.findByCodeIgnoreCase(code)
                .orElseThrow(() -> new BusinessException(ErrorCode.PROMO_CODE_INVALID));
        if (!isValidPromotion(promotion, bookingAmount)) {
            throw new BusinessException(ErrorCode.PROMO_CODE_INVALID);
        }
        return calculateDiscountAmount(promotion, bookingAmount);
    }

    private boolean isValidPromotion(final Promotion promotion, final BigDecimal bookingAmount) {
        if (Boolean.FALSE.equals(promotion.getActive())) {
            return false;
        }
        LocalDate today = LocalDate.now();
        if (promotion.getValidFrom() != null && today.isBefore(promotion.getValidFrom())) {
            return false;
        }
        if (promotion.getValidUntil() != null && today.isAfter(promotion.getValidUntil())) {
            return false;
        }
        if (promotion.getMaxRedemptions() != null && promotion.getUsageCount() != null
                && promotion.getUsageCount() >= promotion.getMaxRedemptions()) {
            return false;
        }
        if (promotion.getMinBookingAmount() != null && bookingAmount != null
                && bookingAmount.compareTo(promotion.getMinBookingAmount()) < 0) {
            return false;
        }
        return true;
    }

    private BigDecimal calculateDiscountAmount(final Promotion promotion, final BigDecimal bookingAmount) {
        if (bookingAmount == null) {
            return BigDecimal.ZERO;
        }
        BigDecimal discount;
        if (promotion.getDiscountType() == DiscountType.PERCENTAGE) {
            discount = bookingAmount
                    .multiply(promotion.getValue())
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        } else {
            discount = promotion.getValue();
        }
        if (promotion.getMaxDiscountAmount() != null) {
            discount = discount.min(promotion.getMaxDiscountAmount());
        }
        return discount.min(bookingAmount).max(BigDecimal.ZERO);
    }
}
