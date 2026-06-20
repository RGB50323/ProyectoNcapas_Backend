package com.uca.ecommerce.repository;

import com.uca.ecommerce.common.Enums.OrderStatus;
import com.uca.ecommerce.domain.entities.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, UUID> {
    List<OrderItem> findByOrderId(UUID orderId);
    List<OrderItem> findByProductId(UUID productId);
    List<OrderItem> findBySellerId(UUID sellerId);
    boolean existsByOrderIdAndProductIdAndVariantId(
            UUID orderId, UUID productId, UUID variantId);

    boolean existsByProductIdAndOrderCustomerUuidAndOrderStatusIn(
            UUID productId, UUID customerId, List<OrderStatus> statuses);

    List<OrderItem> findByOrderCustomerUuidAndOrderStatusIn(
            UUID customerId, List<OrderStatus> statuses);
}