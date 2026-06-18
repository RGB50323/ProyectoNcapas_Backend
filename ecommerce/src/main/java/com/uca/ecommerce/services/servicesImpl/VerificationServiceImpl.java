package com.uca.ecommerce.services.servicesImpl;

import com.uca.ecommerce.common.Enums.VerificationStageStatus;
import com.uca.ecommerce.common.mappers.VerificationMapper;
import com.uca.ecommerce.domain.dto.request.verification.CreateVerificationRequest;
import com.uca.ecommerce.domain.dto.request.verification.PatchVerificationRequest;
import com.uca.ecommerce.domain.dto.request.verification.UpdateVerificationRequest;
import com.uca.ecommerce.domain.dto.response.VerificationResponse;
import com.uca.ecommerce.domain.entities.Product;
import com.uca.ecommerce.domain.entities.User;
import com.uca.ecommerce.domain.entities.Verification;
import com.uca.ecommerce.exceptions.NotFoundException;
import com.uca.ecommerce.exceptions.VerificationLockedException;
import com.uca.ecommerce.repository.ProductRepository;
import com.uca.ecommerce.repository.UserRepository;
import com.uca.ecommerce.repository.VerificationRepository;
import com.uca.ecommerce.services.VerificationService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class VerificationServiceImpl implements VerificationService {

    private final VerificationRepository verificationRepository;
    private final VerificationMapper verificationMapper;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    @Override
    public List<VerificationResponse> getAllVerifications() {
        return verificationMapper.toDtoList(verificationRepository.findAll());
    }

    @Override
    public VerificationResponse getVerificationById(UUID id) {
        return verificationMapper.toDto(verificationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Verification not found")));
    }

    @Override
    public List<VerificationResponse> getVerificationsByProductId(UUID productId) {
        return verificationMapper.toDtoList(verificationRepository.findByProductId(productId));
    }

    @Override
    public List<VerificationResponse> getVerificationsByVerifiedBy(UUID verifiedByUuid) {
        return verificationMapper.toDtoList(verificationRepository.findByVerifiedByUuid(verifiedByUuid));
    }

    @Override
    public VerificationResponse createVerification(CreateVerificationRequest request) {
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new NotFoundException("Product not found"));

        User verifiedBy = userRepository.findById(request.getVerifiedBy())
                .orElseThrow(() -> new NotFoundException("User not found"));

        Verification toSave = verificationMapper.toEntityCreate(request, product, verifiedBy);
        checkAndSetVerifiedAt(toSave);

        return verificationMapper.toDto(verificationRepository.save(toSave));
    }

    @Override
    @Transactional
    public VerificationResponse updateVerification(UpdateVerificationRequest request, UUID id) {
        Verification existing = verificationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Verification not found"));

        checkIfLocked(existing);

        Verification toSave = verificationMapper.toEntityUpdate(request, id, existing);
        checkAndSetVerifiedAt(toSave);
        return verificationMapper.toDto(verificationRepository.save(toSave));
    }

    @Override
    @Transactional
    public VerificationResponse patchVerification(PatchVerificationRequest request, UUID id) {
        Verification existing = verificationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Verification not found"));

        checkIfLocked(existing);

        Verification toSave = verificationMapper.toEntityPatch(request, existing);
        checkAndSetVerifiedAt(toSave);

        return verificationMapper.toDto(verificationRepository.save(toSave));
    }

    @Override
    public VerificationResponse deleteVerification(UUID id) {
        Verification existing = verificationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Verification not found"));

        checkIfLocked(existing);

        VerificationResponse response = verificationMapper.toDto(existing);
        verificationRepository.deleteById(id);
        return response;
    }

    private void checkAndSetVerifiedAt(Verification verification) {
        boolean allStagesDone = verification.getMaterialCheck() != VerificationStageStatus.PENDING &&
                verification.getConstructionCheck() != VerificationStageStatus.PENDING &&
                verification.getFactoryCodeCheck() != VerificationStageStatus.PENDING &&
                verification.getFinalInspection() != VerificationStageStatus.PENDING;

        if (allStagesDone) {
            verification.setVerifiedAt(LocalDateTime.now());
        }
    }

    private void checkIfLocked(Verification verification) {
        boolean allPassed = verification.getMaterialCheck() == VerificationStageStatus.PASSED &&
                verification.getConstructionCheck() == VerificationStageStatus.PASSED &&
                verification.getFactoryCodeCheck() == VerificationStageStatus.PASSED &&
                verification.getFinalInspection() == VerificationStageStatus.PASSED;

        if (allPassed) {
            throw new VerificationLockedException("Verification is finished and cannot be modified");
        }
    }

}