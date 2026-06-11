package com.uca.ecommerce.controller;

import com.uca.ecommerce.domain.dto.request.orderItem.CreateOrderItemRequest;
import com.uca.ecommerce.domain.dto.request.orderItem.PatchOrderItemRequest;
import com.uca.ecommerce.domain.dto.request.orderItem.UpdateOrderItemRequest;
import com.uca.ecommerce.domain.dto.response.GeneralResponse;
import com.uca.ecommerce.services.OrderItemService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/order-items")
@RequiredArgsConstructor
public class OrderItemController extends BaseController {

    private final OrderItemService orderItemService;

    @GetMapping("/")
    public ResponseEntity<GeneralResponse> getAllOrderItems() {
        return buildResponse(
                "Order items retrieved successfully",
                HttpStatus.OK,
                orderItemService.getAllOrderItems()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<GeneralResponse> getOrderItemById(@PathVariable UUID id) {
        return buildResponse(
                "Order item retrieved successfully",
                HttpStatus.OK,
                orderItemService.getOrderItemById(id)
        );
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<GeneralResponse> getItemsByOrder(@PathVariable UUID orderId) {
        return buildResponse(
                "Order items retrieved successfully",
                HttpStatus.OK,
                orderItemService.getItemsByOrderId(orderId)
        );
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<GeneralResponse> getItemsByProduct(@PathVariable UUID productId) {
        return buildResponse(
                "Order items retrieved successfully",
                HttpStatus.OK,
                orderItemService.getItemsByProductId(productId)
        );
    }

    @GetMapping("/seller/{sellerId}")
    public ResponseEntity<GeneralResponse> getItemsBySeller(@PathVariable UUID sellerId) {
        return buildResponse(
                "Order items retrieved successfully",
                HttpStatus.OK,
                orderItemService.getItemsBySellerId(sellerId)
        );
    }

    @PostMapping("/create")
    public ResponseEntity<GeneralResponse> createOrderItem(
            @Valid @RequestBody CreateOrderItemRequest request) {
        return buildResponse(
                "Order item created successfully",
                HttpStatus.CREATED,
                orderItemService.createOrderItem(request)
        );
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<GeneralResponse> updateOrderItem(
            @Valid @RequestBody UpdateOrderItemRequest request,
            @PathVariable UUID id) {
        return buildResponse(
                "Order item updated successfully",
                HttpStatus.OK,
                orderItemService.updateOrderItem(request, id)
        );
    }

    @PatchMapping("/patch/{id}")
    public ResponseEntity<GeneralResponse> patchOrderItem(
            @Valid @RequestBody PatchOrderItemRequest request,
            @PathVariable UUID id) {
        return buildResponse(
                "Order item partially updated successfully",
                HttpStatus.OK,
                orderItemService.patchOrderItem(request, id)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<GeneralResponse> deleteOrderItem(@PathVariable UUID id) {
        return buildResponse(
                "Order item deleted successfully",
                HttpStatus.OK,
                orderItemService.deleteOrderItem(id)
        );
    }
}