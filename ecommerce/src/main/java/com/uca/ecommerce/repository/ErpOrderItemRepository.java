package com.uca.ecommerce.repository;

import com.uca.ecommerce.domain.entities.ErpOrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ErpOrderItemRepository extends JpaRepository<ErpOrderItem, UUID> {
    List<ErpOrderItem> findByErpOrderId(UUID erpOrderId);
}
