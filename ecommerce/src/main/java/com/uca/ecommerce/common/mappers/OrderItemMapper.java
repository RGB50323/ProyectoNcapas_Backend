package com.uca.ecommerce.common.mappers;

import com.uca.ecommerce.domain.dto.request.orderItem.CreateOrderItemRequest;
import com.uca.ecommerce.domain.dto.request.orderItem.PatchOrderItemRequest;
import com.uca.ecommerce.domain.dto.request.orderItem.UpdateOrderItemRequest;
import com.uca.ecommerce.domain.dto.response.OrderItemResponse;
import com.uca.ecommerce.domain.entities.*;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
public class OrderItemMapper {

    public OrderItem toEntityCreate(
            CreateOrderItemRequest request,
            Order order,
            Product product,
            ProductVariant variant,
            SellerProfile seller
    ) {
        BigDecimal unitPrice = calculateUnitPrice(product, variant);
        BigDecimal totalPrice = unitPrice.multiply(BigDecimal.valueOf(request.getQuantity()));

        return OrderItem.builder()
                .order(order)
                .product(product)
                .variant(variant)
                .seller(seller)
                .quantity(request.getQuantity())
                .unitPrice(unitPrice)
                .totalPrice(totalPrice)
                .build();
    }

    public OrderItem toEntityUpdate(
            UpdateOrderItemRequest request,
            OrderItem existing,
            Product product,
            ProductVariant variant,
            SellerProfile seller
    ) {
        BigDecimal unitPrice = calculateUnitPrice(product, variant);
        BigDecimal totalPrice = unitPrice.multiply(BigDecimal.valueOf(request.getQuantity()));

        return OrderItem.builder()
                .id(existing.getId())
                .order(existing.getOrder())
                .product(product)
                .variant(variant)
                .seller(seller)
                .quantity(request.getQuantity())
                .unitPrice(unitPrice)
                .totalPrice(totalPrice)
                .build();
    }

    public OrderItem toEntityPatch(
            PatchOrderItemRequest request,
            OrderItem existing,
            ProductVariant variant
    ) {
        ProductVariant finalVariant = Boolean.TRUE.equals(request.getRemoveVariant())
                ? null
                : variant != null ? variant : existing.getVariant();

        Integer finalQuantity = request.getQuantity() != null
                ? request.getQuantity() : existing.getQuantity();

        BigDecimal unitPrice = calculateUnitPrice(existing.getProduct(), finalVariant);
        BigDecimal totalPrice = unitPrice.multiply(BigDecimal.valueOf(finalQuantity));

        return OrderItem.builder()
                .id(existing.getId())
                .order(existing.getOrder())
                .product(existing.getProduct())
                .variant(finalVariant)
                .seller(existing.getSeller())
                .quantity(finalQuantity)
                .unitPrice(unitPrice)
                .totalPrice(totalPrice)
                .build();
    }

    public OrderItemResponse toDto(OrderItem item) {
        return OrderItemResponse.builder()
                .id(item.getId())
                .orderId(item.getOrder().getId())
                .productId(item.getProduct().getId())
                .productName(item.getProduct().getName())
                .variantId(item.getVariant() != null ? item.getVariant().getId() : null)
                .variantSize(item.getVariant() != null ? item.getVariant().getSize() : null)
                .variantColorName(item.getVariant() != null
                        ? item.getVariant().getColorName() : null)
                .sellerId(item.getSeller().getId())
                .sellerStoreName(item.getSeller().getStoreName())
                .quantity(item.getQuantity())
                .unitPrice(item.getUnitPrice())
                .totalPrice(item.getTotalPrice())
                .build();
    }

    public List<OrderItemResponse> toDtoList(List<OrderItem> items) {
        return items.stream()
                .map(this::toDto)
                .toList();
    }

    // Precio base del producto + delta de la variante si existe
    private BigDecimal calculateUnitPrice(Product product, ProductVariant variant) {
        BigDecimal base = product.getPrice();
        if (variant != null) {
            base = base.add(variant.getPriceDelta());
        }
        return base;
    }
}