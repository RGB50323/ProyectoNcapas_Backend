package com.uca.ecommerce.common.mappers;

import com.uca.ecommerce.domain.dto.request.sellerProfile.CreateSellerProfileRequest;
import com.uca.ecommerce.domain.dto.request.sellerProfile.UpdateSellerProfileRequest;
import com.uca.ecommerce.domain.dto.response.SellerProfileResponse;
import com.uca.ecommerce.domain.dto.response.UserResponse;
import com.uca.ecommerce.domain.entities.SellerProfile;
import com.uca.ecommerce.domain.entities.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class SellerProfileMapper {

    public SellerProfile toEntityCreate(CreateSellerProfileRequest request, User user) {
        return SellerProfile.builder()
                .storeName(request.getStoreName())
                .storeDescription(request.getStoreDescription())
                .user(user)
                .build();
    }

    public SellerProfile toEntityUpdate(UpdateSellerProfileRequest request, SellerProfile existing) {
        return SellerProfile.builder()
                .id(existing.getId())
                .storeName(request.getStoreName())
                .storeDescription(request.getStoreDescription())
                .user(existing.getUser())
                .build();
    }

    public SellerProfileResponse toDto(SellerProfile sellerProfile) {
        User user = sellerProfile.getUser();
        return SellerProfileResponse.builder()
                .id(sellerProfile.getId())
                .storeName(sellerProfile.getStoreName())
                .storeDescription(sellerProfile.getStoreDescription())
                .rating(sellerProfile.getRating())
                .totalSales(sellerProfile.getTotalSales())
                .verified(sellerProfile.isVerified())
                .user(UserResponse.builder()
                        .uuid(user.getUuid())
                        .firstName(user.getFirstName())
                        .lastName(user.getLastName())
                        .email(user.getEmail())
                        .phone(user.getPhone())
                        .role(user.getRole().name())
                        .build())
                .build();
    }

    public List<SellerProfileResponse> toDtoList(List<SellerProfile> sellerProfiles) {
        return sellerProfiles.stream().map(this::toDto).toList();
    }
}