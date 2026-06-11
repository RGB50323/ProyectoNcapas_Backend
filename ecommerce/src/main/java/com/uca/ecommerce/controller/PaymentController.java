package com.uca.ecommerce.controller;

import com.uca.ecommerce.common.Enums.PaymentStatus;
import com.uca.ecommerce.domain.dto.request.payment.CreatePaymentRequest;
import com.uca.ecommerce.domain.dto.request.payment.PatchPaymentRequest;
import com.uca.ecommerce.domain.dto.request.payment.UpdatePaymentRequest;
import com.uca.ecommerce.domain.dto.response.GeneralResponse;
import com.uca.ecommerce.services.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentController extends BaseController {

    private final PaymentService paymentService;

    @GetMapping("/")
    public ResponseEntity<GeneralResponse> getAllPayments() {
        return buildResponse(
                "Payments retrieved successfully",
                HttpStatus.OK,
                paymentService.getAllPayments()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<GeneralResponse> getPaymentById(@PathVariable UUID id) {
        return buildResponse(
                "Payment retrieved successfully",
                HttpStatus.OK,
                paymentService.getPaymentById(id)
        );
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<GeneralResponse> getPaymentsByOrder(@PathVariable UUID orderId) {
        return buildResponse(
                "Payments retrieved successfully",
                HttpStatus.OK,
                paymentService.getPaymentsByOrderId(orderId)
        );
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<GeneralResponse> getPaymentsByStatus(
            @PathVariable PaymentStatus status) {
        return buildResponse(
                "Payments retrieved successfully",
                HttpStatus.OK,
                paymentService.getPaymentsByStatus(status)
        );
    }

    @PostMapping("/create")
    public ResponseEntity<GeneralResponse> createPayment(
            @Valid @RequestBody CreatePaymentRequest request) {
        return buildResponse(
                "Payment processed successfully",
                HttpStatus.CREATED,
                paymentService.createPayment(request)
        );
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<GeneralResponse> updatePayment(
            @Valid @RequestBody UpdatePaymentRequest request,
            @PathVariable UUID id) {
        return buildResponse(
                "Payment updated successfully",
                HttpStatus.OK,
                paymentService.updatePayment(request, id)
        );
    }

    @PatchMapping("/patch/{id}")
    public ResponseEntity<GeneralResponse> patchPayment(
            @Valid @RequestBody PatchPaymentRequest request,
            @PathVariable UUID id) {
        return buildResponse(
                "Payment partially updated successfully",
                HttpStatus.OK,
                paymentService.patchPayment(request, id)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<GeneralResponse> deletePayment(@PathVariable UUID id) {
        return buildResponse(
                "Payment deleted successfully",
                HttpStatus.OK,
                paymentService.deletePayment(id)
        );
    }
}