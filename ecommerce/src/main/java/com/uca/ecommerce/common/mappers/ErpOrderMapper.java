package com.uca.ecommerce.common.mappers;

import com.uca.ecommerce.domain.dto.response.ErpOrderItemResponse;
import com.uca.ecommerce.domain.dto.response.ErpOrderResponse;
import com.uca.ecommerce.domain.entities.ErpOrder;
import com.uca.ecommerce.domain.entities.ErpOrderItem;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ErpOrderMapper {

    public ErpOrderResponse toDto(ErpOrder erpOrder) {
        return ErpOrderResponse.builder()
                .id(erpOrder.getId())
                .erpReference(erpOrder.getErpReference())
                .sourceOrderId(erpOrder.getSourceOrderId())
                .customerId(erpOrder.getCustomerId())
                .customerName(erpOrder.getCustomerName())
                .customerEmail(erpOrder.getCustomerEmail())
                .shippingAddress(erpOrder.getShippingAddress())
                .subtotal(erpOrder.getSubtotal())
                .shippingCost(erpOrder.getShippingCost())
                .discountAmount(erpOrder.getDiscountAmount())
                .total(erpOrder.getTotal())
                .orderDate(erpOrder.getOrderDate())
                .receivedAt(erpOrder.getReceivedAt())
                .items(toItemDtoList(erpOrder.getItems()))
                .build();
    }

    public List<ErpOrderResponse> toDtoList(List<ErpOrder> erpOrders) {
        return erpOrders.stream()
                .map(this::toDto)
                .toList();
    }

    private ErpOrderItemResponse toItemDto(ErpOrderItem item) {
        return ErpOrderItemResponse.builder()
                .id(item.getId())
                .sourceOrderItemId(item.getSourceOrderItemId())
                .productId(item.getProductId())
                .sku(item.getSku())
                .productName(item.getProductName())
                .variantId(item.getVariantId())
                .variantDescription(item.getVariantDescription())
                .quantity(item.getQuantity())
                .unitPrice(item.getUnitPrice())
                .totalPrice(item.getTotalPrice())
                .build();
    }

    private List<ErpOrderItemResponse> toItemDtoList(List<ErpOrderItem> items) {
        return items.stream()
                .map(this::toItemDto)
                .toList();
    }
}
