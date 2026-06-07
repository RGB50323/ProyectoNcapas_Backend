package com.uca.ecommerce.services;

import com.uca.ecommerce.domain.dto.request.verification.CreateVerificationRequest;
import com.uca.ecommerce.domain.dto.request.verification.PatchVerificationRequest;
import com.uca.ecommerce.domain.dto.request.verification.UpdateVerificationRequest;
import com.uca.ecommerce.domain.dto.response.VerificationResponse;

import java.util.List;
import java.util.UUID;

public interface VerificationService {
    List<VerificationResponse> getAllVerifications();
    VerificationResponse getVerificationById(Long id);
    List<VerificationResponse> getVerificationsByProductId(UUID productId);
    List<VerificationResponse> getVerificationsByVerifiedBy(UUID verifiedByUuid);
    VerificationResponse createVerification(CreateVerificationRequest request);
    VerificationResponse updateVerification(UpdateVerificationRequest request, Long id);
    VerificationResponse patchVerification(PatchVerificationRequest request, Long id);
    VerificationResponse deleteVerification(Long id);
}