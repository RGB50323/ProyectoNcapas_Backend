package com.uca.ecommerce.controller;

import com.uca.ecommerce.domain.dto.request.sellerProfile.CreateSellerProfileRequest;
import com.uca.ecommerce.domain.dto.request.sellerProfile.UpdateSellerProfileRequest;
import com.uca.ecommerce.domain.dto.response.GeneralResponse;
import com.uca.ecommerce.services.SellerProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/seller_profiles")
@RequiredArgsConstructor
public class SellerProfileController extends BaseController {

    private final SellerProfileService sellerProfileService;

    @GetMapping("/")
    public ResponseEntity<GeneralResponse> getAllSellerProfiles() {
        return buildResponse("Seller profiles retrieved successfully", HttpStatus.OK, sellerProfileService.getAllSellerProfiles());
    }

    @GetMapping("/{id}")
    public ResponseEntity<GeneralResponse> getSellerProfileById(@PathVariable UUID id) {
        return buildResponse("Seller profile retrieved successfully", HttpStatus.OK, sellerProfileService.getSellerProfileId(id));
    }

    @PreAuthorize("hasRole('BUYER')")
    @PostMapping("/create")
    public ResponseEntity<GeneralResponse> createSellerProfile(@Valid @RequestBody CreateSellerProfileRequest request) {
        return buildResponse("Seller profile created successfully", HttpStatus.CREATED, sellerProfileService.createSellerProfile(request));
    }

    @PreAuthorize("hasAnyRole('ADMIN','SELLER')")
    @PutMapping("/update/{id}")
    public ResponseEntity<GeneralResponse> updateSellerProfile(@Valid @RequestBody UpdateSellerProfileRequest request, @PathVariable UUID id) {
        return buildResponse("Seller profile updated successfully", HttpStatus.OK, sellerProfileService.updateSellerProfile(request, id));
    }

    @PreAuthorize("hasAnyRole('ADMIN','SELLER')")
    @DeleteMapping("/{id}")
    public ResponseEntity<GeneralResponse> deleteSellerProfile(@PathVariable UUID id) {
        return buildResponse("Seller profile deleted successfully", HttpStatus.OK, sellerProfileService.deleteSellerProfile(id));
    }
}