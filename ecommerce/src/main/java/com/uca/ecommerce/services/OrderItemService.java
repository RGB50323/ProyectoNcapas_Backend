package com.uca.ecommerce.services;

import com.uca.ecommerce.domain.dto.request.orderItem.CreateOrderItemRequest;
import com.uca.ecommerce.domain.dto.request.orderItem.PatchOrderItemRequest;
import com.uca.ecommerce.domain.dto.request.orderItem.UpdateOrderItemRequest;
import com.uca.ecommerce.domain.dto.response.OrderItemResponse;

import java.util.List;
import java.util.UUID;

public interface OrderItemService {
    List<OrderItemResponse> getAllOrderItems();
    List<OrderItemResponse> getItemsByOrderId(UUID orderId);
    List<OrderItemResponse> getItemsByProductId(UUID productId);
    List<OrderItemResponse> getItemsBySellerId(UUID sellerId);
    OrderItemResponse getOrderItemById(UUID id);
    OrderItemResponse createOrderItem(CreateOrderItemRequest request);
    OrderItemResponse updateOrderItem(UpdateOrderItemRequest request, UUID id);
    OrderItemResponse patchOrderItem(PatchOrderItemRequest request, UUID id);
    OrderItemResponse deleteOrderItem(UUID id);
}