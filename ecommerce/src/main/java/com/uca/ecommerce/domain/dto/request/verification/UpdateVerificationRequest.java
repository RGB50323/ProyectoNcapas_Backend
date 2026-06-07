package com.uca.ecommerce.domain.dto.request.verification;

import com.uca.ecommerce.common.Enums.VerificationStageStatus;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateVerificationRequest {

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