package com.uca.ecommerce.services;

import com.uca.ecommerce.domain.dto.request.sellerProfile.CreateSellerProfileRequest;
import com.uca.ecommerce.domain.dto.request.sellerProfile.UpdateSellerProfileRequest;
import com.uca.ecommerce.domain.dto.response.AuthResponse;
import com.uca.ecommerce.domain.dto.response.SellerProfileResponse;

import java.util.List;
import java.util.UUID;

public interface SellerProfileService {

    List<SellerProfileResponse> getAllSellerProfiles();
    SellerProfileResponse getSellerProfileId(UUID id);
    SellerProfileResponse updateSellerProfile(UpdateSellerProfileRequest request, UUID id);
    AuthResponse createSellerProfile(CreateSellerProfileRequest request);
    AuthResponse deleteSellerProfile(UUID id);
}