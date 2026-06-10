package com.uca.ecommerce.domain.dto.response;

import lombok.*;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DropProductResponse {

    private UUID id;

    private UUID dropId;
    private String dropTitle;

    private UUID productId;
    private String productName;
    private String productSku;
}
