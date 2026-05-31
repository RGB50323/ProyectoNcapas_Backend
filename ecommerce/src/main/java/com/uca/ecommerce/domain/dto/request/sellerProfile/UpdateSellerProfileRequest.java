package com.uca.ecommerce.domain.dto.request.sellerProfile;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateSellerProfileRequest {

    @NotBlank(message = "Store name is required")
    private String storeName;

    @NotBlank(message = "Store description is required")
    private String storeDescription;
}
