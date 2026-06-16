package com.uca.ecommerce.domain.dto.response;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DiscountTypeResponse {
    private String value;
    private String label;
    private boolean usesValue;
}
