package com.uca.ecommerce.repository;

import com.uca.ecommerce.domain.entities.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, UUID> {
    List<CartItem> findByUser_Uuid(UUID userId);
    Optional<CartItem> findByUser_UuidAndProduct_IdAndVariant_Id(UUID userId, UUID productId, UUID variantId);
    long countByProduct_Id(UUID productId);
}
