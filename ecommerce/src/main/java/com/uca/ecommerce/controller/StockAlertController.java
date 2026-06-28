package com.uca.ecommerce.controller;

import com.uca.ecommerce.domain.dto.request.stockAlert.CreateStockAlertRequest;
import com.uca.ecommerce.domain.dto.response.GeneralResponse;
import com.uca.ecommerce.services.StockAlertService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/stock-alerts")
@RequiredArgsConstructor
public class StockAlertController extends BaseController {

    private final StockAlertService alertService;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/")
    public ResponseEntity<GeneralResponse> getAllAlerts() {
        return buildResponse(
                "Stock alerts retrieved successfully",
                HttpStatus.OK,
                alertService.getAllAlerts()
        );
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'SELLER', 'BUYER')")
    @GetMapping("/{id}")
    public ResponseEntity<GeneralResponse> getAlertById(@PathVariable UUID id) {
        return buildResponse(
                "Stock alert retrieved successfully",
                HttpStatus.OK,
                alertService.getAlertById(id)
        );
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'SELLER', 'BUYER')")
    @GetMapping("/user/{userId}")
    public ResponseEntity<GeneralResponse> getAlertsByUser(@PathVariable UUID userId) {
        return buildResponse(
                "Stock alerts retrieved successfully",
                HttpStatus.OK,
                alertService.getAlertsByUserId(userId)
        );
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'SELLER')")
    @GetMapping("/product/{productId}")
    public ResponseEntity<GeneralResponse> getAlertsByProduct(@PathVariable UUID productId) {
        return buildResponse(
                "Stock alerts retrieved successfully",
                HttpStatus.OK,
                alertService.getAlertsByProductId(productId)
        );
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'BUYER')")
    @PostMapping("/create")
    public ResponseEntity<GeneralResponse> createAlert(@Valid @RequestBody CreateStockAlertRequest request) {
        return buildResponse(
                "Stock alert created successfully",
                HttpStatus.CREATED,
                alertService.createAlert(request)
        );
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'BUYER')")
    @DeleteMapping("/{id}")
    public ResponseEntity<GeneralResponse> deleteAlert(@PathVariable UUID id) {
        return buildResponse(
                "Stock alert deleted successfully",
                HttpStatus.OK,
                alertService.deleteAlert(id)
        );
    }
}