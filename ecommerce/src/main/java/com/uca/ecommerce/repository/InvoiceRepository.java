package com.uca.ecommerce.repository;

import com.uca.ecommerce.domain.entities.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, UUID> {
    Optional<Invoice> findByOrderId(UUID orderId);
    boolean existsByOrderId(UUID orderId);
    Optional<Invoice> findByControlNumber(String controlNumber);
}
