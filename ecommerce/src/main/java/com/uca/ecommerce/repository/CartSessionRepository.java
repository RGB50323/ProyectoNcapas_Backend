package com.uca.ecommerce.repository;

import com.uca.ecommerce.common.Enums.CartSessionStatus;
import com.uca.ecommerce.domain.entities.CartSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CartSessionRepository extends JpaRepository<CartSession, UUID> {
    Optional<CartSession> findByUserUuidAndStatus(UUID userId, CartSessionStatus status);
    List<CartSession> findByStatus(CartSessionStatus status);
    long countByStatus(CartSessionStatus status);
    Optional<CartSession> findByUserUuidAndStatusAndAbandonedManually(
            UUID userId, CartSessionStatus status, Boolean abandonedManually);
}