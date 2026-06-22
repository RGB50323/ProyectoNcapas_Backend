package com.uca.ecommerce.common.mappers;

import com.uca.ecommerce.common.Enums.StoreRequestStatus;
import com.uca.ecommerce.domain.dto.request.storeRequest.CreateStoreRequestRequest;
import com.uca.ecommerce.domain.dto.response.StoreRequestResponse;
import com.uca.ecommerce.domain.dto.response.UserResponse;
import com.uca.ecommerce.domain.entities.StoreRequest;
import com.uca.ecommerce.domain.entities.User;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class StoreRequestMapper {

    public StoreRequest toEntityCreate(CreateStoreRequestRequest request, User user) {
        return StoreRequest.builder()
                .user(user)
                .storeName(request.getStoreName())
                .storeDescription(request.getStoreDescription())
                .storeCategory(request.getStoreCategory())
                .location(request.getLocation())
                .monthlySalesEstimate(request.getMonthlySalesEstimate())
                .status(StoreRequestStatus.PENDIENTE)
                .build();
    }

    public StoreRequestResponse toDto(StoreRequest request, int cooldownDays) {
        User user = request.getUser();

        LocalDateTime nextEligibleAt = null;
        boolean eligibleToReapply = false;
        if (request.getStatus() == StoreRequestStatus.RECHAZADA && request.getReviewedAt() != null) {
            nextEligibleAt = request.getReviewedAt().plusDays(cooldownDays);
            eligibleToReapply = LocalDateTime.now().isAfter(nextEligibleAt);
        }

        return StoreRequestResponse.builder()
                .id(request.getId())
                .storeName(request.getStoreName())
                .storeDescription(request.getStoreDescription())
                .storeCategory(request.getStoreCategory())
                .location(request.getLocation())
                .monthlySalesEstimate(request.getMonthlySalesEstimate())
                .status(request.getStatus())
                .reviewNote(request.getReviewNote())
                .createdAt(request.getCreatedAt())
                .reviewedAt(request.getReviewedAt())
                .eligibleToReapply(eligibleToReapply)
                .nextEligibleAt(nextEligibleAt)
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

    public List<StoreRequestResponse> toDtoList(List<StoreRequest> requests, int cooldownDays) {
        return requests.stream().map(r -> toDto(r, cooldownDays)).toList();
    }
}
