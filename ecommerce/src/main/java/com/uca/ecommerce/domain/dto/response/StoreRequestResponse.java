package com.uca.ecommerce.domain.dto.response;

import com.uca.ecommerce.common.Enums.StoreCategory;
import com.uca.ecommerce.common.Enums.StoreRequestStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class StoreRequestResponse {
    private UUID id;
    private String storeName;
    private String storeDescription;
    private StoreCategory storeCategory;
    private String location;
    private int monthlySalesEstimate;
    private StoreRequestStatus status;
    private String reviewNote;
    private LocalDateTime createdAt;
    private LocalDateTime reviewedAt;
    private UserResponse user;

    // solo aplica cuando status es RECHAZADA
    private boolean eligibleToReapply;
    private LocalDateTime nextEligibleAt;
}
