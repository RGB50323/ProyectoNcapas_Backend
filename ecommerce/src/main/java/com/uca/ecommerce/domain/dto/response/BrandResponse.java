package com.uca.ecommerce.domain.dto.response;

import lombok.*;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BrandResponse {
    private UUID id;
    private String name;
    private String slug;
    private String logoUrl;
}