package com.uca.ecommerce.domain.dto.request.shippingMethod;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PatchShippingMethodRequest {

    @Size(min = 1, max = 100, message = "Shipping method name must contain between 1 and 100 characters")
    private String name;

    @DecimalMin(value = "0.0", message = "Shipping fee must be greater than or equal to 0")
    @Digits(integer = 8, fraction = 2, message = "Shipping fee must have up to 8 integer digits and 2 decimals")
    private BigDecimal fee;

    @Size(min = 1, max = 50, message = "ETA must contain between 1 and 50 characters")
    private String eta;

    private Boolean active;
}
