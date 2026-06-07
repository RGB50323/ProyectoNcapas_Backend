package com.uca.ecommerce.controller;

import com.uca.ecommerce.domain.dto.request.verification.CreateVerificationRequest;
import com.uca.ecommerce.domain.dto.request.verification.PatchVerificationRequest;
import com.uca.ecommerce.domain.dto.request.verification.UpdateVerificationRequest;
import com.uca.ecommerce.domain.dto.response.GeneralResponse;
import com.uca.ecommerce.services.VerificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/verifications")
@RequiredArgsConstructor
public class VerificationController extends BaseController {

    private final VerificationService verificationService;

    @GetMapping("/")
    public ResponseEntity<GeneralResponse> getAllVerifications() {
        return buildResponse("Verifications retrieved successfully", HttpStatus.OK, verificationService.getAllVerifications());
    }

    @GetMapping("/{id}")
    public ResponseEntity<GeneralResponse> getVerificationById(@PathVariable Long id) {
        return buildResponse("Verification retrieved successfully", HttpStatus.OK, verificationService.getVerificationById(id));
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<GeneralResponse> getVerificationsByProductId(@PathVariable UUID productId) {
        return buildResponse("Verifications retrieved successfully", HttpStatus.OK, verificationService.getVerificationsByProductId(productId));
    }

    @GetMapping("/verified-by/{verifiedByUuid}")
    public ResponseEntity<GeneralResponse> getVerificationsByVerifiedBy(@PathVariable UUID verifiedByUuid) {
        return buildResponse("Verifications retrieved successfully", HttpStatus.OK, verificationService.getVerificationsByVerifiedBy(verifiedByUuid));
    }

    @PostMapping("/create")
    public ResponseEntity<GeneralResponse> createVerification(@Valid @RequestBody CreateVerificationRequest request) {
        return buildResponse("Verification created successfully", HttpStatus.CREATED, verificationService.createVerification(request));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<GeneralResponse> updateVerification(@Valid @RequestBody UpdateVerificationRequest request, @PathVariable Long id) {
        return buildResponse("Verification updated successfully", HttpStatus.OK, verificationService.updateVerification(request, id));
    }

    @PatchMapping("/patch/{id}")
    public ResponseEntity<GeneralResponse> patchVerification(@Valid @RequestBody PatchVerificationRequest request, @PathVariable Long id) {
        return buildResponse("Verification patched successfully", HttpStatus.OK, verificationService.patchVerification(request, id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<GeneralResponse> deleteVerification(@PathVariable Long id) {
        return buildResponse("Verification deleted successfully", HttpStatus.OK, verificationService.deleteVerification(id));
    }
}