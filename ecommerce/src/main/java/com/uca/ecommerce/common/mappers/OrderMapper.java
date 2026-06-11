package com.uca.ecommerce.common.mappers;

import com.uca.ecommerce.domain.dto.request.order.CreateOrderRequest;
import com.uca.ecommerce.domain.dto.request.order.PatchOrderRequest;
import com.uca.ecommerce.domain.dto.request.order.UpdateOrderRequest;
import com.uca.ecommerce.domain.dto.response.OrderResponse;
import com.uca.ecommerce.domain.entities.*;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
public class OrderMapper {

    public Order toEntityCreate(
            CreateOrderRequest request,
            User customer,
            Address shippingAddress,
            ShippingMethod shippingMethod,
            Coupon coupon,
            BigDecimal subtotal,
            BigDecimal discountAmount,
            BigDecimal total
    ) {
        return Order.builder()
                .customer(customer)
                .shippingAddress(shippingAddress)
                .shippingMethod(shippingMethod)
                .coupon(coupon)
                .subtotal(subtotal)
                .shippingCost(shippingMethod.getFee())
                .discountAmount(discountAmount)
                .total(total)
                .notes(request.getNotes())
                .build();
    }

    public Order toEntityUpdate(
            UpdateOrderRequest request,
            Order existing,
            Address shippingAddress,
            ShippingMethod shippingMethod,
            Coupon coupon
    ) {
        return Order.builder()
                .id(existing.getId())
                .customer(existing.getCustomer())
                .shippingAddress(shippingAddress)
                .shippingMethod(shippingMethod)
                .coupon(coupon)
                .status(request.getStatus())
                .subtotal(existing.getSubtotal())
                .shippingCost(shippingMethod.getFee())
                .discountAmount(existing.getDiscountAmount())
                .total(existing.getSubtotal()
                        .add(shippingMethod.getFee())
                        .subtract(existing.getDiscountAmount()))
                .trackingNumber(request.getTrackingNumber())
                .notes(request.getNotes())
                .createdAt(existing.getCreatedAt())
                .build();
    }

    public OrderResponse toDto(Order order) {
        return OrderResponse.builder()
                .id(order.getId())
                .customerId(order.getCustomer().getUuid())
                .customerFullName(order.getCustomer().getFirstName()
                        + " " + order.getCustomer().getLastName())
                .shippingAddressId(order.getShippingAddress().getId())
                .shippingAddressStreet(order.getShippingAddress().getStreet())
                .shippingAddressCity(order.getShippingAddress().getCity())
                .shippingAddressCountry(order.getShippingAddress().getCountry())
                .shippingMethodId(order.getShippingMethod().getId())
                .shippingMethodName(order.getShippingMethod().getName())
                .couponId(order.getCoupon() != null ? order.getCoupon().getId() : null)
                .couponCode(order.getCoupon() != null ? order.getCoupon().getCode() : null)
                .status(order.getStatus())
                .subtotal(order.getSubtotal())
                .shippingCost(order.getShippingCost())
                .discountAmount(order.getDiscountAmount())
                .total(order.getTotal())
                .trackingNumber(order.getTrackingNumber())
                .notes(order.getNotes())
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .build();
    }

    public List<OrderResponse> toDtoList(List<Order> orders) {
        return orders.stream()
                .map(this::toDto)
                .toList();
    }

    public Order toEntityPatch(
            PatchOrderRequest request,
            Order existing,
            Address shippingAddress,
            ShippingMethod shippingMethod,
            Coupon coupon
    ) {
        ShippingMethod finalShippingMethod = shippingMethod != null
                ? shippingMethod : existing.getShippingMethod();

        BigDecimal newTotal = existing.getSubtotal()
                .add(finalShippingMethod.getFee())
                .subtract(existing.getDiscountAmount());

        return Order.builder()
                .id(existing.getId())
                .customer(existing.getCustomer())
                .shippingAddress(shippingAddress != null
                        ? shippingAddress : existing.getShippingAddress())
                .shippingMethod(finalShippingMethod)
                .coupon(Boolean.TRUE.equals(request.getRemoveCoupon())
                        ? null
                        : coupon != null ? coupon : existing.getCoupon())
                .status(request.getStatus() != null
                        ? request.getStatus() : existing.getStatus())
                .subtotal(existing.getSubtotal())
                .shippingCost(finalShippingMethod.getFee())
                .discountAmount(existing.getDiscountAmount())
                .total(newTotal)
                .trackingNumber(Boolean.TRUE.equals(request.getRemoveTrackingNumber())
                        ? null
                        : request.getTrackingNumber() != null
                          ? request.getTrackingNumber() : existing.getTrackingNumber())
                .notes(Boolean.TRUE.equals(request.getRemoveNotes())
                        ? null
                        : request.getNotes() != null
                          ? request.getNotes() : existing.getNotes())
                .createdAt(existing.getCreatedAt())
                .build();
    }
}