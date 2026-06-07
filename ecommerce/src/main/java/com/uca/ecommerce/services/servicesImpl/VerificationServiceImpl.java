package com.uca.ecommerce.services.servicesImpl;

import com.uca.ecommerce.common.mappers.VerificationMapper;
import com.uca.ecommerce.domain.dto.request.verification.CreateVerificationRequest;
import com.uca.ecommerce.domain.dto.request.verification.PatchVerificationRequest;
import com.uca.ecommerce.domain.dto.request.verification.UpdateVerificationRequest;
import com.uca.ecommerce.domain.dto.response.VerificationResponse;
import com.uca.ecommerce.domain.entities.Product;
import com.uca.ecommerce.domain.entities.User;
import com.uca.ecommerce.domain.entities.Verification;
import com.uca.ecommerce.exceptions.NotFoundException;
import com.uca.ecommerce.repository.ProductRepository;
import com.uca.ecommerce.repository.UserRepository;
import com.uca.ecommerce.repository.VerificationRepository;
import com.uca.ecommerce.services.VerificationService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
    public VerificationResponse getVerificationById(Long id) {
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

        return verificationMapper.toDto(
                verificationRepository.save(verificationMapper.toEntityCreate(request, product, verifiedBy))
        );
    }

    @Override
    @Transactional
    public VerificationResponse updateVerification(UpdateVerificationRequest request, Long id) {
        Verification existing = verificationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Verification not found"));

        return verificationMapper.toDto(
                verificationRepository.save(verificationMapper.toEntityUpdate(request, id, existing))
        );
    }

    @Override
    @Transactional
    public VerificationResponse patchVerification(PatchVerificationRequest request, Long id) {
        Verification existing = verificationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Verification not found"));

        return verificationMapper.toDto(
                verificationRepository.save(verificationMapper.toEntityPatch(request, existing))
        );
    }

    @Override
    public VerificationResponse deleteVerification(Long id) {
        VerificationResponse existing = this.getVerificationById(id);
        verificationRepository.deleteById(id);
        return existing;
    }
}