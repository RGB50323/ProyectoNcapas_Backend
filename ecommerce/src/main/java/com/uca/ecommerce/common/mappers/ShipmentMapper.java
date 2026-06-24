package com.uca.ecommerce.common.mappers;

import com.uca.ecommerce.domain.dto.response.ShipmentResponse;
import com.uca.ecommerce.domain.entities.Order;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class ShipmentMapper {

    public ShipmentResponse toDto(Order order, String trackingNumber, LocalDate estimatedDelivery) {
        return ShipmentResponse.builder()
                .orderId(order.getId())
                .trackingNumber(trackingNumber)
                .shippingMethod(order.getShippingMethod() != null ? order.getShippingMethod().getName() : null)
                .orderStatus(order.getStatus())
                .estimatedDelivery(estimatedDelivery)
                .build();
    }
}
