package com.uca.ecommerce.domain.dto.request.verification;

import com.uca.ecommerce.common.Enums.VerificationStageStatus;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PatchVerificationRequest {

    private VerificationStageStatus materialCheck;
    private VerificationStageStatus constructionCheck;
    private VerificationStageStatus factoryCodeCheck;
    private VerificationStageStatus finalInspection;
    private String notes;
}