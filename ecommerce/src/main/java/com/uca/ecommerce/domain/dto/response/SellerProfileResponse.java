package com.uca.ecommerce.domain.dto.response;

import com.uca.ecommerce.common.Enums.StoreCategory;
import com.uca.ecommerce.domain.entities.User;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class SellerProfileResponse {
    private UUID id;
    private String storeName;
    private String storeDescription;
    private StoreCategory storeCategory;
    private String location;
    private BigDecimal rating;
    private int totalSales;
    private boolean verified;
    private UserResponse user;
}