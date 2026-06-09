package com.uca.ecommerce.domain.dto.response;

import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StockAlertResponse {
    private UUID id;
    private UUID userId;
    private String userFullName;
    private UUID productId;
    private String productName;
    private LocalDateTime notifiedAt;
}