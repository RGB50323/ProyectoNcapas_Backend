package com.uca.ecommerce.domain.dto.request.stockAlert;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateStockAlertRequest {

    @NotNull(message = "User ID is required")
    private UUID userId;

    @NotNull(message = "Product ID is required")
    private UUID productId;
}