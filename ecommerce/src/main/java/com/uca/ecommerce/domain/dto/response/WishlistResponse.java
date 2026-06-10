package com.uca.ecommerce.domain.dto.response;

import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WishlistResponse {

    private UUID id;

    private UUID userId;

    private UUID productId;
    private String productName;
    private String productSku;

    private LocalDateTime addedAt;
}
