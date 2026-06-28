package com.uca.ecommerce.domain.dto.request.storeRequest;

import com.uca.ecommerce.common.Enums.StoreRequestStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReviewStoreRequestRequest {

    @NotNull(message = "Decision is required")
    private StoreRequestStatus decision;

    private String reviewNote;
}
