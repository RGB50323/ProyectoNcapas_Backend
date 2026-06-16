package com.uca.ecommerce.domain.dto.request.sellerProfile;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VerifySellerProfileRequest {

    @NotNull(message = "Verified flag is required")
    private Boolean verified;
}
