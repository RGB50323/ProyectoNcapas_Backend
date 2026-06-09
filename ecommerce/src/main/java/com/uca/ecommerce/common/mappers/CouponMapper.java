package com.uca.ecommerce.common.mappers;

import com.uca.ecommerce.domain.dto.request.coupon.CreateCouponRequest;
import com.uca.ecommerce.domain.dto.request.coupon.PatchCouponRequest;
import com.uca.ecommerce.domain.dto.request.coupon.UpdateCouponRequest;
import com.uca.ecommerce.domain.dto.response.CouponResponse;
import com.uca.ecommerce.domain.entities.Coupon;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CouponMapper {

    public Coupon toEntityCreate(CreateCouponRequest request) {
        return Coupon.builder()
                .code(request.getCode())
                .label(request.getLabel())
                .type(request.getType())
                .value(request.getValue())
                .minOrderAmount(request.getMinOrderAmount())
                .maxUses(request.getMaxUses())
                .active(request.getActive() == null || request.getActive())
                .expiresAt(request.getExpiresAt())
                .build();
    }

    public Coupon toEntityUpdate(UpdateCouponRequest request, Coupon existing) {
        return Coupon.builder()
                .id(existing.getId())
                .code(request.getCode())
                .label(request.getLabel())
                .type(request.getType())
                .value(request.getValue())
                .minOrderAmount(request.getMinOrderAmount())
                .maxUses(request.getMaxUses())
                .usesCount(existing.getUsesCount())
                .active(request.getActive())
                .expiresAt(request.getExpiresAt())
                .createdAt(existing.getCreatedAt())
                .build();
    }

    public Coupon toEntityPatch(PatchCouponRequest request, Coupon existing) {
        return Coupon.builder()
                .id(existing.getId())
                .code(request.getCode() != null ? request.getCode() : existing.getCode())
                .label(request.getLabel() != null ? request.getLabel() : existing.getLabel())
                .type(request.getType() != null ? request.getType() : existing.getType())
                .value(request.getValue() != null ? request.getValue() : existing.getValue())
                .minOrderAmount(Boolean.TRUE.equals(request.getRemoveMinOrderAmount())
                        ? null
                        : request.getMinOrderAmount() != null
                          ? request.getMinOrderAmount()
                          : existing.getMinOrderAmount())
                .maxUses(Boolean.TRUE.equals(request.getRemoveMaxUses())
                        ? null
                        : request.getMaxUses() != null
                          ? request.getMaxUses()
                          : existing.getMaxUses())
                .usesCount(existing.getUsesCount())
                .active(request.getActive() != null ? request.getActive() : existing.isActive())
                .expiresAt(Boolean.TRUE.equals(request.getRemoveExpiresAt())
                        ? null
                        : request.getExpiresAt() != null
                          ? request.getExpiresAt()
                          : existing.getExpiresAt())
                .createdAt(existing.getCreatedAt())
                .build();
    }

    public CouponResponse toDto(Coupon coupon) {
        return CouponResponse.builder()
                .id(coupon.getId())
                .code(coupon.getCode())
                .label(coupon.getLabel())
                .type(coupon.getType())
                .value(coupon.getValue())
                .minOrderAmount(coupon.getMinOrderAmount())
                .maxUses(coupon.getMaxUses())
                .usesCount(coupon.getUsesCount())
                .active(coupon.isActive())
                .expiresAt(coupon.getExpiresAt())
                .createdAt(coupon.getCreatedAt())
                .build();
    }

    public List<CouponResponse> toDtoList(List<Coupon> coupons) {
        return coupons.stream()
                .map(this::toDto)
                .toList();
    }
}
