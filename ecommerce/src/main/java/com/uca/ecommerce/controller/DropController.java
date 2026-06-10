package com.uca.ecommerce.controller;

import com.uca.ecommerce.domain.dto.request.drop.CreateDropRequest;
import com.uca.ecommerce.domain.dto.request.drop.PatchDropRequest;
import com.uca.ecommerce.domain.dto.request.drop.UpdateDropRequest;
import com.uca.ecommerce.domain.dto.response.GeneralResponse;
import com.uca.ecommerce.services.DropService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/drops")
@RequiredArgsConstructor
public class DropController extends BaseController {

    private final DropService dropService;

    @GetMapping("/")
    public ResponseEntity<GeneralResponse> getAllDrops() {
        return buildResponse(
                "Drops retrieved successfully",
                HttpStatus.OK,
                dropService.getAllDrops()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<GeneralResponse> getDropById(@PathVariable UUID id) {
        return buildResponse(
                "Drop retrieved successfully",
                HttpStatus.OK,
                dropService.getDropById(id)
        );
    }

    @PreAuthorize("hasAnyRole('ADMIN','SELLER')")
    @PostMapping("/create")
    public ResponseEntity<GeneralResponse> createDrop(@Valid @RequestBody CreateDropRequest request) {
        return buildResponse(
                "Drop created successfully",
                HttpStatus.CREATED,
                dropService.createDrop(request)
        );
    }

    @PreAuthorize("hasAnyRole('ADMIN','SELLER')")
    @PutMapping("/update/{id}")
    public ResponseEntity<GeneralResponse> updateDrop(@Valid @RequestBody UpdateDropRequest request, @PathVariable UUID id) {
        return buildResponse(
                "Drop updated successfully",
                HttpStatus.OK,
                dropService.updateDrop(request, id)
        );
    }

    @PreAuthorize("hasAnyRole('ADMIN','SELLER')")
    @PatchMapping("/patch/{id}")
    public ResponseEntity<GeneralResponse> patchDrop(@Valid @RequestBody PatchDropRequest request, @PathVariable UUID id) {
        return buildResponse(
                "Drop partially updated successfully",
                HttpStatus.OK,
                dropService.patchDrop(request, id)
        );
    }

    @PreAuthorize("hasAnyRole('ADMIN','SELLER')")
    @DeleteMapping("/{id}")
    public ResponseEntity<GeneralResponse> deleteDrop(@PathVariable UUID id) {
        return buildResponse(
                "Drop deleted successfully",
                HttpStatus.OK,
                dropService.deleteDrop(id)
        );
    }
}
