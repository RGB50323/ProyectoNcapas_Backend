package com.uca.ecommerce.domain.dto.response;

import lombok.*;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductImageResponse {

    private UUID id;

    private UUID productId;
    private String productName;
    private String productSku;
    private String productSlug;

    private String url;
    private String altText;
    private Boolean primaryImage;
    private Integer sortOrder;
}