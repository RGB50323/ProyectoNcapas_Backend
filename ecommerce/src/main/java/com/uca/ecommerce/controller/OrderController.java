package com.uca.ecommerce.controller;

import com.uca.ecommerce.common.Enums.OrderStatus;
import com.uca.ecommerce.domain.dto.request.order.CreateOrderRequest;
import com.uca.ecommerce.domain.dto.request.order.PatchOrderRequest;
import com.uca.ecommerce.domain.dto.request.order.UpdateOrderRequest;
import com.uca.ecommerce.domain.dto.response.GeneralResponse;
import com.uca.ecommerce.domain.entities.User;
import com.uca.ecommerce.security.CurrentUserProvider;
import com.uca.ecommerce.services.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController extends BaseController {

    private final OrderService orderService;
    private final CurrentUserProvider currentUserProvider; // ← para verificar ownership

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/")
    public ResponseEntity<GeneralResponse> getAllOrders() {
        return buildResponse(
                "Orders retrieved successfully",
                HttpStatus.OK,
                orderService.getAllOrders()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<GeneralResponse> getOrderById(@PathVariable UUID id) {
        return buildResponse(
                "Order retrieved successfully",
                HttpStatus.OK,
                orderService.getOrderById(id)
        );
    }

    // ← Buyer solo puede ver sus propias órdenes
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<GeneralResponse> getOrdersByCustomer(
            @PathVariable UUID customerId) {

        User currentUser = currentUserProvider.getCurrentUser();
        boolean isAdmin = currentUser.getRole().name().equals("ADMIN");
        boolean isOwner = currentUser.getUuid().equals(customerId);

        if (!isAdmin && !isOwner) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        return buildResponse(
                "Orders retrieved successfully",
                HttpStatus.OK,
                orderService.getOrdersByCustomerId(customerId)
        );
    }

    @PreAuthorize("hasAnyRole('ADMIN','SELLER')")
    @GetMapping("/status/{status}")
    public ResponseEntity<GeneralResponse> getOrdersByStatus(
            @PathVariable OrderStatus status) {
        return buildResponse(
                "Orders retrieved successfully",
                HttpStatus.OK,
                orderService.getOrdersByStatus(status)
        );
    }

    @PreAuthorize("hasRole('BUYER')")
    @PostMapping("/create")
    public ResponseEntity<GeneralResponse> createOrder(
            @Valid @RequestBody CreateOrderRequest request) {
        return buildResponse(
                "Order created successfully",
                HttpStatus.CREATED,
                orderService.createOrder(request)
        );
    }

    @PreAuthorize("hasAnyRole('ADMIN','SELLER')")
    @PutMapping("/update/{id}")
    public ResponseEntity<GeneralResponse> updateOrder(
            @Valid @RequestBody UpdateOrderRequest request,
            @PathVariable UUID id) {
        return buildResponse(
                "Order updated successfully",
                HttpStatus.OK,
                orderService.updateOrder(request, id)
        );
    }

    @PreAuthorize("hasAnyRole('ADMIN','SELLER')")
    @PatchMapping("/patch/{id}")
    public ResponseEntity<GeneralResponse> patchOrder(
            @Valid @RequestBody PatchOrderRequest request,
            @PathVariable UUID id) {
        return buildResponse(
                "Order partially updated successfully",
                HttpStatus.OK,
                orderService.patchOrder(request, id)
        );
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<GeneralResponse> deleteOrder(@PathVariable UUID id) {
        return buildResponse(
                "Order deleted successfully",
                HttpStatus.OK,
                orderService.deleteOrder(id)
        );
    }
}
