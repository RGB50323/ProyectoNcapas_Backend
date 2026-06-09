package com.uca.ecommerce.controller;

import com.uca.ecommerce.domain.dto.request.stockAlert.CreateStockAlertRequest;
import com.uca.ecommerce.domain.dto.response.GeneralResponse;
import com.uca.ecommerce.services.StockAlertService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/stock-alerts")
@RequiredArgsConstructor
public class StockAlertController extends BaseController {

    private final StockAlertService alertService;

    @GetMapping("/")
    public ResponseEntity<GeneralResponse> getAllAlerts() {
        return buildResponse(
                "Stock alerts retrieved successfully",
                HttpStatus.OK,
                alertService.getAllAlerts()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<GeneralResponse> getAlertById(@PathVariable UUID id) {
        return buildResponse(
                "Stock alert retrieved successfully",
                HttpStatus.OK,
                alertService.getAlertById(id)
        );
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<GeneralResponse> getAlertsByUser(@PathVariable UUID userId) {
        return buildResponse(
                "Stock alerts retrieved successfully",
                HttpStatus.OK,
                alertService.getAlertsByUserId(userId)
        );
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<GeneralResponse> getAlertsByProduct(@PathVariable UUID productId) {
        return buildResponse(
                "Stock alerts retrieved successfully",
                HttpStatus.OK,
                alertService.getAlertsByProductId(productId)
        );
    }

    @PostMapping("/create")
    public ResponseEntity<GeneralResponse> createAlert(@Valid @RequestBody CreateStockAlertRequest request) {
        return buildResponse(
                "Stock alert created successfully",
                HttpStatus.CREATED,
                alertService.createAlert(request)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<GeneralResponse> deleteAlert(@PathVariable UUID id) {
        return buildResponse(
                "Stock alert deleted successfully",
                HttpStatus.OK,
                alertService.deleteAlert(id)
        );
    }
}