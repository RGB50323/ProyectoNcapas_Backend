package com.uca.ecommerce.common.mappers;

import com.uca.ecommerce.domain.dto.request.verification.CreateVerificationRequest;
import com.uca.ecommerce.domain.dto.request.verification.PatchVerificationRequest;
import com.uca.ecommerce.domain.dto.request.verification.UpdateVerificationRequest;
import com.uca.ecommerce.domain.dto.response.VerificationResponse;
import com.uca.ecommerce.domain.entities.Product;
import com.uca.ecommerce.domain.entities.User;
import com.uca.ecommerce.domain.entities.Verification;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class VerificationMapper {

    public Verification toEntityCreate(CreateVerificationRequest request, Product product, User verifiedBy) {
        return Verification.builder()
                .product(product)
                .verifiedBy(verifiedBy)
                .materialCheck(request.getMaterialCheck())
                .constructionCheck(request.getConstructionCheck())
                .factoryCodeCheck(request.getFactoryCodeCheck())
                .finalInspection(request.getFinalInspection())
                .notes(request.getNotes())
                .build();
    }

    public Verification toEntityUpdate(UpdateVerificationRequest request, UUID id, Verification existing) {
        return Verification.builder()
                .id(id)
                .product(existing.getProduct())
                .verifiedBy(existing.getVerifiedBy())
                .materialCheck(request.getMaterialCheck())
                .constructionCheck(request.getConstructionCheck())
                .factoryCodeCheck(request.getFactoryCodeCheck())
                .finalInspection(request.getFinalInspection())
                .notes(request.getNotes())
                .build();
    }

    public Verification toEntityPatch(PatchVerificationRequest request, Verification existing) {
        return Verification.builder()
                .id(existing.getId())
                .product(existing.getProduct())
                .verifiedBy(existing.getVerifiedBy())
                .materialCheck(request.getMaterialCheck() != null ? request.getMaterialCheck() : existing.getMaterialCheck())
                .constructionCheck(request.getConstructionCheck() != null ? request.getConstructionCheck() : existing.getConstructionCheck())
                .factoryCodeCheck(request.getFactoryCodeCheck() != null ? request.getFactoryCodeCheck() : existing.getFactoryCodeCheck())
                .finalInspection(request.getFinalInspection() != null ? request.getFinalInspection() : existing.getFinalInspection())
                .notes(request.getNotes() != null ? request.getNotes() : existing.getNotes())
                .build();
    }

    public VerificationResponse toDto(Verification verification) {
        return VerificationResponse.builder()
                .id(verification.getId())
                .productId(verification.getProduct().getId())
                .productName(verification.getProduct().getName())
                .verifiedById(verification.getVerifiedBy().getUuid())
                .verifiedByFirstName(verification.getVerifiedBy().getFirstName())
                .verifiedByLastName(verification.getVerifiedBy().getLastName())
                .materialCheck(verification.getMaterialCheck())
                .constructionCheck(verification.getConstructionCheck())
                .factoryCodeCheck(verification.getFactoryCodeCheck())
                .finalInspection(verification.getFinalInspection())
                .notes(verification.getNotes())
                .verifiedAt(verification.getVerifiedAt())
                .createdAt(verification.getCreatedAt())
                .build();
    }

    public List<VerificationResponse> toDtoList(List<Verification> verifications) {
        return verifications.stream().map(this::toDto).toList();
    }
}