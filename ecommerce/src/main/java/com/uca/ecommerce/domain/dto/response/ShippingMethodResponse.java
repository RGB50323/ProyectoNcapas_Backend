package com.uca.ecommerce.domain.dto.response;

import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ShippingMethodResponse {
    private UUID id;
    private String name;
    private BigDecimal fee;
    private String eta;
    private Boolean active;
}
