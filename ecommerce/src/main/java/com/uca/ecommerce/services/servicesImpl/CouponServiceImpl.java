package com.uca.ecommerce.services.servicesImpl;

import com.uca.ecommerce.common.Enums.DiscountType;
import com.uca.ecommerce.common.mappers.CouponMapper;
import com.uca.ecommerce.domain.dto.request.coupon.CreateCouponRequest;
import com.uca.ecommerce.domain.dto.request.coupon.PatchCouponRequest;
import com.uca.ecommerce.domain.dto.request.coupon.PreviewCouponRequest;
import com.uca.ecommerce.domain.dto.request.coupon.UpdateCouponRequest;
import com.uca.ecommerce.domain.dto.response.CouponPreviewResponse;
import com.uca.ecommerce.domain.dto.response.CouponResponse;
import com.uca.ecommerce.domain.entities.CartItem;
import com.uca.ecommerce.domain.entities.Coupon;
import com.uca.ecommerce.domain.entities.ShippingMethod;
import com.uca.ecommerce.domain.entities.User;
import com.uca.ecommerce.exceptions.FieldAlreadyExistsException;
import com.uca.ecommerce.exceptions.InvalidCouponException;
import com.uca.ecommerce.exceptions.NotFoundException;
import com.uca.ecommerce.repository.CartItemRepository;
import com.uca.ecommerce.repository.CouponRepository;
import com.uca.ecommerce.repository.ShippingMethodRepository;
import com.uca.ecommerce.security.CurrentUserProvider;
import com.uca.ecommerce.services.CouponService;
import com.uca.ecommerce.services.discount.DiscountContext;
import com.uca.ecommerce.services.discount.DiscountStrategyResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CouponServiceImpl implements CouponService {

    private final CouponRepository couponRepository;
    private final CartItemRepository cartItemRepository;
    private final ShippingMethodRepository shippingMethodRepository;
    private final CouponMapper couponMapper;
    private final CurrentUserProvider currentUserProvider;
    private final DiscountStrategyResolver discountStrategyResolver;

    @Override
    public List<CouponResponse> getAllCoupons() {
        return couponMapper.toDtoList(couponRepository.findAll());
    }

    @Override
    public CouponResponse getCouponById(UUID id) {
        Coupon coupon = couponRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Coupon not found"));
        return couponMapper.toDto(coupon);
    }

    @Override
    public CouponResponse createCoupon(CreateCouponRequest request) {
        if (couponRepository.existsByCode(request.getCode())) {
            throw new FieldAlreadyExistsException("Coupon code already exists");
        }

        validatePercentageValue(request.getType(), request.getValue());

        Coupon saved = couponRepository.save(couponMapper.toEntityCreate(request));
        return couponMapper.toDto(saved);
    }

    @Override
    public CouponResponse updateCoupon(UpdateCouponRequest request, UUID id) {
        Coupon existing = couponRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Coupon not found"));

        if (!existing.getCode().equals(request.getCode())
                && couponRepository.existsByCode(request.getCode())) {
            throw new FieldAlreadyExistsException("Coupon code already exists");
        }

        validatePercentageValue(request.getType(), request.getValue());

        Coupon saved = couponRepository.save(couponMapper.toEntityUpdate(request, existing));
        return couponMapper.toDto(saved);
    }

    @Override
    public CouponResponse patchCoupon(PatchCouponRequest request, UUID id) {
        Coupon existing = couponRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Coupon not found"));

        if (request.getCode() != null
                && !existing.getCode().equals(request.getCode())
                && couponRepository.existsByCode(request.getCode())) {
            throw new FieldAlreadyExistsException("Coupon code already exists");
        }

        DiscountType finalType = request.getType() != null ? request.getType() : existing.getType();
        BigDecimal finalValue = request.getValue() != null ? request.getValue() : existing.getValue();
        validatePercentageValue(finalType, finalValue);

        Coupon saved = couponRepository.save(couponMapper.toEntityPatch(request, existing));
        return couponMapper.toDto(saved);
    }

    @Override
    public CouponResponse deleteCoupon(UUID id) {
        Coupon existing = couponRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Coupon not found"));

        couponRepository.deleteById(id);
        return couponMapper.toDto(existing);
    }

    @Override
    public CouponPreviewResponse previewCoupon(PreviewCouponRequest request) {
        User user = currentUserProvider.getCurrentUser();

        Coupon coupon = couponRepository.findByCode(request.getCode())
                .orElseThrow(() -> new NotFoundException("Coupon not found"));

        if (!coupon.isActive()) {
            throw new InvalidCouponException("Coupon is not active");
        }
        if (coupon.getExpiresAt() != null && coupon.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new InvalidCouponException("Coupon has expired");
        }
        if (coupon.getMaxUses() != null && coupon.getUsesCount() >= coupon.getMaxUses()) {
            throw new InvalidCouponException("Coupon usage limit reached");
        }

        List<CartItem> cart = cartItemRepository.findByUser_Uuid(user.getUuid());
        if (cart.isEmpty()) {
            throw new InvalidCouponException("Cart is empty");
        }

        List<DiscountContext.LineItem> items = cart.stream()
                .map(item -> DiscountContext.LineItem.builder()
                        .productId(item.getProduct().getId())
                        .unitPrice(item.getProduct().getPrice().add(item.getVariant().getPriceDelta()))
                        .quantity(item.getQuantity())
                        .build())
                .toList();

        BigDecimal subtotal = items.stream()
                .map(item -> item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal shippingCost = BigDecimal.ZERO;
        if (request.getShippingMethodId() != null) {
            ShippingMethod shippingMethod = shippingMethodRepository.findById(request.getShippingMethodId())
                    .orElseThrow(() -> new NotFoundException("Shipping method not found"));
            shippingCost = shippingMethod.getFee();
        }

        if (coupon.getMinOrderAmount() != null && subtotal.compareTo(coupon.getMinOrderAmount()) < 0) {
            throw new InvalidCouponException("Order subtotal does not meet the minimum amount for this coupon");
        }

        DiscountContext context = DiscountContext.builder()
                .subtotal(subtotal)
                .shippingCost(shippingCost)
                .items(items)
                .build();

        BigDecimal discount = discountStrategyResolver.resolve(coupon.getType())
                .calculate(context, coupon)
                .min(subtotal.add(shippingCost))
                .setScale(2, RoundingMode.HALF_UP);

        BigDecimal total = subtotal.add(shippingCost).subtract(discount).max(BigDecimal.ZERO);

        return CouponPreviewResponse.builder()
                .couponId(coupon.getId())
                .couponCode(coupon.getCode())
                .discountType(coupon.getType())
                .subtotal(subtotal.setScale(2, RoundingMode.HALF_UP))
                .shippingCost(shippingCost.setScale(2, RoundingMode.HALF_UP))
                .discountAmount(discount)
                .total(total.setScale(2, RoundingMode.HALF_UP))
                .build();
    }

    // A percentage coupon cannot exceed 100%
    private void validatePercentageValue(DiscountType type, BigDecimal value) {
        if (type == DiscountType.PERCENTAGE
                && value != null
                && value.compareTo(BigDecimal.valueOf(100)) > 0) {
            throw new InvalidCouponException("Percentage discount cannot exceed 100");
        }
    }
}
