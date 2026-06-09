package com.uca.ecommerce.services;

import com.uca.ecommerce.domain.dto.request.coupon.CreateCouponRequest;
import com.uca.ecommerce.domain.dto.request.coupon.PatchCouponRequest;
import com.uca.ecommerce.domain.dto.request.coupon.PreviewCouponRequest;
import com.uca.ecommerce.domain.dto.request.coupon.UpdateCouponRequest;
import com.uca.ecommerce.domain.dto.response.CouponPreviewResponse;
import com.uca.ecommerce.domain.dto.response.CouponResponse;

import java.util.List;
import java.util.UUID;

public interface CouponService {

    List<CouponResponse> getAllCoupons();

    CouponResponse getCouponById(UUID id);

    CouponResponse createCoupon(CreateCouponRequest request);

    CouponResponse updateCoupon(UpdateCouponRequest request, UUID id);

    CouponResponse patchCoupon(PatchCouponRequest request, UUID id);

    CouponResponse deleteCoupon(UUID id);

    CouponPreviewResponse previewCoupon(PreviewCouponRequest request);
}
