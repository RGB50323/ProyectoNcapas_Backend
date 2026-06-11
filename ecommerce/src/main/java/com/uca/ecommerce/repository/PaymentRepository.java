package com.uca.ecommerce.repository;

import com.uca.ecommerce.common.Enums.PaymentStatus;
import com.uca.ecommerce.domain.entities.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, UUID> {
    List<Payment> findByOrderId(UUID orderId);
    List<Payment> findByStatus(PaymentStatus status);
    boolean existsByOrderId(UUID orderId);
}