package com.uca.ecommerce.domain.dto.request.verification;

import com.uca.ecommerce.common.Enums.VerificationStageStatus;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateVerificationRequest {

    @NotNull(message = "Product ID is required")
    private UUID productId;

    @NotNull(message = "Verified by is required")
    private UUID verifiedBy;

    @NotNull(message = "Material check is required")
    private VerificationStageStatus materialCheck;

    @NotNull(message = "Construction check is required")
    private VerificationStageStatus constructionCheck;

    @NotNull(message = "Factory code check is required")
    private VerificationStageStatus factoryCodeCheck;

    @NotNull(message = "Final inspection is required")
    private VerificationStageStatus finalInspection;

    private String notes;
}