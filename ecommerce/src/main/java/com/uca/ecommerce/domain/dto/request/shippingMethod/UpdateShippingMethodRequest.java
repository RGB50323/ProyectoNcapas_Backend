package com.uca.ecommerce.domain.dto.request.shippingMethod;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateShippingMethodRequest {

    @NotBlank(message = "Shipping method name is required")
    @Size(max = 100, message = "Shipping method name must not exceed 100 characters")
    private String name;

    @NotNull(message = "Shipping fee is required")
    @DecimalMin(value = "0.0", message = "Shipping fee must be greater than or equal to 0")
    @Digits(integer = 8, fraction = 2, message = "Shipping fee must have up to 8 integer digits and 2 decimals")
    private BigDecimal fee;

    @NotBlank(message = "ETA is required")
    @Size(max = 50, message = "ETA must not exceed 50 characters")
    private String eta;

    @NotNull(message = "Active flag is required")
    private Boolean active;
}
