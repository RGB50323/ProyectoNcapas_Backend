package com.uca.ecommerce.domain.dto.response;

import lombok.*;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductBadgeResponse {
    private UUID id;
    private UUID productId;
    private String productName;
    private String label;
}