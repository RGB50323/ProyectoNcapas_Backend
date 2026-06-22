package com.uca.ecommerce.domain.dto.request.storeRequest;

import com.uca.ecommerce.common.Enums.StoreCategory;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateStoreRequestRequest {

    @NotBlank(message = "Store name is required")
    private String storeName;

    @NotBlank(message = "Store description is required")
    private String storeDescription;

    @NotNull(message = "Store category is required")
    private StoreCategory storeCategory;

    @NotBlank(message = "Location is required")
    private String location;

    @NotNull(message = "Monthly sales estimate is required")
    @Min(value = 0, message = "Monthly sales estimate cannot be negative")
    private Integer monthlySalesEstimate;
}
