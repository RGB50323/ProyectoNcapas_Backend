package com.uca.ecommerce.controller;

import com.uca.ecommerce.domain.dto.request.shippingMethod.CreateShippingMethodRequest;
import com.uca.ecommerce.domain.dto.request.shippingMethod.PatchShippingMethodRequest;
import com.uca.ecommerce.domain.dto.request.shippingMethod.UpdateShippingMethodRequest;
import com.uca.ecommerce.domain.dto.response.GeneralResponse;
import com.uca.ecommerce.services.ShippingMethodService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/shipping-methods")
@RequiredArgsConstructor
public class ShippingMethodController extends BaseController {

    private final ShippingMethodService shippingMethodService;

    @GetMapping("/")
    public ResponseEntity<GeneralResponse> getAllShippingMethods() {
        return buildResponse(
                "Shipping methods retrieved successfully",
                HttpStatus.OK,
                shippingMethodService.getAllShippingMethods()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<GeneralResponse> getShippingMethodById(@PathVariable UUID id) {
        return buildResponse(
                "Shipping method retrieved successfully",
                HttpStatus.OK,
                shippingMethodService.getShippingMethodById(id)
        );
    }

    @PostMapping("/create")
    public ResponseEntity<GeneralResponse> createShippingMethod(@Valid @RequestBody CreateShippingMethodRequest request) {
        return buildResponse(
                "Shipping method created successfully",
                HttpStatus.CREATED,
                shippingMethodService.createShippingMethod(request)
        );
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<GeneralResponse> updateShippingMethod(@Valid @RequestBody UpdateShippingMethodRequest request, @PathVariable UUID id) {
        return buildResponse(
                "Shipping method updated successfully",
                HttpStatus.OK,
                shippingMethodService.updateShippingMethod(request, id)
        );
    }

    @PatchMapping("/patch/{id}")
    public ResponseEntity<GeneralResponse> patchShippingMethod(@Valid @RequestBody PatchShippingMethodRequest request, @PathVariable UUID id) {
        return buildResponse(
                "Shipping method partially updated successfully",
                HttpStatus.OK,
                shippingMethodService.patchShippingMethod(request, id)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<GeneralResponse> deleteShippingMethod(@PathVariable UUID id) {
        return buildResponse(
                "Shipping method deleted successfully",
                HttpStatus.OK,
                shippingMethodService.deleteShippingMethod(id)
        );
    }
}
