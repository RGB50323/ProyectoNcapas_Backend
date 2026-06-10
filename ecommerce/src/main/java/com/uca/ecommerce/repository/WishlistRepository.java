package com.uca.ecommerce.repository;

import com.uca.ecommerce.domain.entities.Wishlist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface WishlistRepository extends JpaRepository<Wishlist, UUID> {
    List<Wishlist> findByUser_Uuid(UUID userId);
    boolean existsByUser_UuidAndProduct_Id(UUID userId, UUID productId);
}
