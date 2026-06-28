package com.uca.ecommerce.services;

import com.uca.ecommerce.common.Enums.OrderStatus;
import com.uca.ecommerce.domain.dto.request.order.CreateOrderRequest;
import com.uca.ecommerce.domain.dto.request.order.PatchOrderRequest;
import com.uca.ecommerce.domain.dto.request.order.UpdateOrderRequest;
import com.uca.ecommerce.domain.dto.response.OrderResponse;

import java.util.List;
import java.util.UUID;

public interface OrderService {
    List<OrderResponse> getAllOrders();
    List<OrderResponse> getOrdersByCustomerId(UUID customerId);
    List<OrderResponse> getOrdersByStatus(OrderStatus status);
    OrderResponse getOrderById(UUID id);
    OrderResponse createOrder(CreateOrderRequest request);
    OrderResponse updateOrder(UpdateOrderRequest request, UUID id);
    OrderResponse deleteOrder(UUID id);
    OrderResponse patchOrder(PatchOrderRequest request, UUID id);
    OrderResponse requestRefund(UUID orderId, UUID customerId);
}