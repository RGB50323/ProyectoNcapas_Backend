package com.uca.ecommerce.repository;

import com.uca.ecommerce.domain.entities.Order;
import com.uca.ecommerce.common.Enums.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<Order, UUID> {
    List<Order> findByCustomerUuid(UUID customerUuid);
    List<Order> findByStatus(OrderStatus status);
    List<Order> findByCustomerUuidAndStatus(UUID customerUuid, OrderStatus status);
}