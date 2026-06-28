package com.uca.ecommerce.repository;

import com.uca.ecommerce.domain.entities.OrderErpExport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface OrderErpExportRepository extends JpaRepository<OrderErpExport, UUID> {
    Optional<OrderErpExport> findByOrderId(UUID orderId);
    Optional<OrderErpExport> findByErpReference(String erpReference);
}
