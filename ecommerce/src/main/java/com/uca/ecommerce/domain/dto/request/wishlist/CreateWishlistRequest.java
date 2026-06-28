package com.uca.ecommerce.domain.dto.request.wishlist;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateWishlistRequest {

    @NotNull(message = "Product ID is required")
    private UUID productId;
}
