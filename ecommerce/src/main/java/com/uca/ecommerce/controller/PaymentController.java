package com.uca.ecommerce.controller;

import com.uca.ecommerce.common.Enums.PaymentStatus;
import com.uca.ecommerce.domain.dto.request.payment.CreatePaymentRequest;
import com.uca.ecommerce.domain.dto.request.payment.PatchPaymentRequest;
import com.uca.ecommerce.domain.dto.request.payment.UpdatePaymentRequest;
import com.uca.ecommerce.domain.dto.response.GeneralResponse;
import com.uca.ecommerce.domain.entities.User;
import com.uca.ecommerce.domain.entities.Payment;
import com.uca.ecommerce.domain.entities.Order;
import com.uca.ecommerce.exceptions.NotFoundException;
import com.uca.ecommerce.repository.OrderRepository;
import com.uca.ecommerce.repository.PaymentRepository;
import com.uca.ecommerce.security.CurrentUserProvider;
import com.uca.ecommerce.services.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentController extends BaseController {

    private final PaymentService paymentService;
    private final CurrentUserProvider currentUserProvider;
    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;

    @PreAuthorize("hasRole('ADMIN')")
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
        User currentUser = currentUserProvider.getCurrentUser();
        boolean isAdmin = currentUser.getRole().name().equals("ADMIN");

        if (!isAdmin) {
            Payment payment = paymentRepository.findById(id)
                    .orElseThrow(() -> new NotFoundException("Payment not found"));
            boolean isOwner = payment.getOrder().getCustomer().getUuid()
                    .equals(currentUser.getUuid());
            if (!isOwner) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
        }

        return buildResponse(
                "Payment retrieved successfully",
                HttpStatus.OK,
                paymentService.getPaymentById(id)
        );
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<GeneralResponse> getPaymentsByOrder(@PathVariable UUID orderId) {
        User currentUser = currentUserProvider.getCurrentUser();
        boolean isAdmin = currentUser.getRole().name().equals("ADMIN");
        boolean isSeller = currentUser.getRole().name().equals("SELLER");

        if (!isAdmin && !isSeller) {
            Order order = orderRepository.findById(orderId)
                    .orElseThrow(() -> new NotFoundException("Order not found"));
            boolean isOwner = order.getCustomer().getUuid()
                    .equals(currentUser.getUuid());
            if (!isOwner) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
        }

        return buildResponse(
                "Payments retrieved successfully",
                HttpStatus.OK,
                paymentService.getPaymentsByOrderId(orderId)
        );
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/status/{status}")
    public ResponseEntity<GeneralResponse> getPaymentsByStatus(
            @PathVariable PaymentStatus status) {
        return buildResponse(
                "Payments retrieved successfully",
                HttpStatus.OK,
                paymentService.getPaymentsByStatus(status)
        );
    }

    @PreAuthorize("hasRole('BUYER')")
    @PostMapping("/create")
    public ResponseEntity<GeneralResponse> createPayment(
            @Valid @RequestBody CreatePaymentRequest request) {
        User currentUser = currentUserProvider.getCurrentUser();

        Order order = orderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new NotFoundException("Order not found"));

        boolean isOwner = order.getCustomer().getUuid()
                .equals(currentUser.getUuid());
        if (!isOwner) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        return buildResponse(
                "Payment processed successfully",
                HttpStatus.CREATED,
                paymentService.createPayment(request)
        );
    }

    @PreAuthorize("hasRole('ADMIN')")
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

    @PreAuthorize("hasRole('ADMIN')")
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

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<GeneralResponse> deletePayment(@PathVariable UUID id) {
        return buildResponse(
                "Payment deleted successfully",
                HttpStatus.OK,
                paymentService.deletePayment(id)
        );
    }
}