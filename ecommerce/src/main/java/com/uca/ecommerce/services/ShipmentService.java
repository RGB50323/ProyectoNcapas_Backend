package com.uca.ecommerce.services;

import com.uca.ecommerce.domain.dto.response.ShipmentResponse;

import java.util.UUID;

public interface ShipmentService {

    ShipmentResponse getByOrderId(UUID orderId);
}
