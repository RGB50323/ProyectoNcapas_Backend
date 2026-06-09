package com.uca.ecommerce.controller;

import com.uca.ecommerce.domain.dto.request.coupon.CreateCouponRequest;
import com.uca.ecommerce.domain.dto.request.coupon.PatchCouponRequest;
import com.uca.ecommerce.domain.dto.request.coupon.PreviewCouponRequest;
import com.uca.ecommerce.domain.dto.request.coupon.UpdateCouponRequest;
import com.uca.ecommerce.domain.dto.response.GeneralResponse;
import com.uca.ecommerce.services.CouponService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/coupons")
@RequiredArgsConstructor
public class CouponController extends BaseController {

    private final CouponService couponService;

    @GetMapping("/")
    public ResponseEntity<GeneralResponse> getAllCoupons() {
        return buildResponse(
                "Coupons retrieved successfully",
                HttpStatus.OK,
                couponService.getAllCoupons()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<GeneralResponse> getCouponById(@PathVariable UUID id) {
        return buildResponse(
                "Coupon retrieved successfully",
                HttpStatus.OK,
                couponService.getCouponById(id)
        );
    }

    @PreAuthorize("hasAnyRole('ADMIN','SELLER')")
    @PostMapping("/create")
    public ResponseEntity<GeneralResponse> createCoupon(@Valid @RequestBody CreateCouponRequest request) {
        return buildResponse(
                "Coupon created successfully",
                HttpStatus.CREATED,
                couponService.createCoupon(request)
        );
    }

    @PreAuthorize("hasAnyRole('ADMIN','SELLER')")
    @PutMapping("/update/{id}")
    public ResponseEntity<GeneralResponse> updateCoupon(@Valid @RequestBody UpdateCouponRequest request, @PathVariable UUID id) {
        return buildResponse(
                "Coupon updated successfully",
                HttpStatus.OK,
                couponService.updateCoupon(request, id)
        );
    }

    @PreAuthorize("hasAnyRole('ADMIN','SELLER')")
    @PatchMapping("/patch/{id}")
    public ResponseEntity<GeneralResponse> patchCoupon(@Valid @RequestBody PatchCouponRequest request, @PathVariable UUID id) {
        return buildResponse(
                "Coupon partially updated successfully",
                HttpStatus.OK,
                couponService.patchCoupon(request, id)
        );
    }

    @PreAuthorize("hasAnyRole('ADMIN','SELLER')")
    @DeleteMapping("/{id}")
    public ResponseEntity<GeneralResponse> deleteCoupon(@PathVariable UUID id) {
        return buildResponse(
                "Coupon deleted successfully",
                HttpStatus.OK,
                couponService.deleteCoupon(id)
        );
    }

    @PostMapping("/preview")
    public ResponseEntity<GeneralResponse> previewCoupon(@Valid @RequestBody PreviewCouponRequest request) {
        return buildResponse(
                "Coupon preview calculated successfully",
                HttpStatus.OK,
                couponService.previewCoupon(request)
        );
    }
}
