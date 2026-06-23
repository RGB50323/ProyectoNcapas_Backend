package com.uca.ecommerce.services;

import com.uca.ecommerce.common.Enums.ProductEventType;

import java.util.UUID;

public interface UserProductEventService {

    void registerCurrentBuyerEvent(UUID productId, ProductEventType type);
}
