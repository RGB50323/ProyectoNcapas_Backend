package com.uca.ecommerce.domain.dto.response;

import com.uca.ecommerce.common.Enums.VerificationStageStatus;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VerificationResponse {

    private UUID id;
    private UUID productId;
    private String productName;
    private UUID verifiedById;
    private String verifiedByFirstName;
    private String verifiedByLastName;
    private VerificationStageStatus materialCheck;
    private VerificationStageStatus constructionCheck;
    private VerificationStageStatus factoryCodeCheck;
    private VerificationStageStatus finalInspection;
    private String notes;
    private LocalDateTime verifiedAt;
    private LocalDateTime createdAt;
}