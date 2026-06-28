package com.uca.ecommerce.repository;

import com.uca.ecommerce.domain.entities.ErpOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ErpOrderRepository extends JpaRepository<ErpOrder, UUID> {
    Optional<ErpOrder> findByErpReference(String erpReference);
    Optional<ErpOrder> findBySourceOrderId(UUID sourceOrderId);
    boolean existsByErpReference(String erpReference);
}
