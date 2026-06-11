package com.uca.ecommerce.controller;

import com.uca.ecommerce.common.Enums.OrderStatus;
import com.uca.ecommerce.domain.dto.request.order.CreateOrderRequest;
import com.uca.ecommerce.domain.dto.request.order.PatchOrderRequest;
import com.uca.ecommerce.domain.dto.request.order.UpdateOrderRequest;
import com.uca.ecommerce.domain.dto.response.GeneralResponse;
import com.uca.ecommerce.services.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController extends BaseController {

    private final OrderService orderService;

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

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<GeneralResponse> getOrdersByCustomer(
            @PathVariable UUID customerId) {
        return buildResponse(
                "Orders retrieved successfully",
                HttpStatus.OK,
                orderService.getOrdersByCustomerId(customerId)
        );
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<GeneralResponse> getOrdersByStatus(
            @PathVariable OrderStatus status) {
        return buildResponse(
                "Orders retrieved successfully",
                HttpStatus.OK,
                orderService.getOrdersByStatus(status)
        );
    }

    @PostMapping("/create")
    public ResponseEntity<GeneralResponse> createOrder(
            @Valid @RequestBody CreateOrderRequest request) {
        return buildResponse(
                "Order created successfully",
                HttpStatus.CREATED,
                orderService.createOrder(request)
        );
    }

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

    @DeleteMapping("/{id}")
    public ResponseEntity<GeneralResponse> deleteOrder(@PathVariable UUID id) {
        return buildResponse(
                "Order deleted successfully",
                HttpStatus.OK,
                orderService.deleteOrder(id)
        );
    }
}