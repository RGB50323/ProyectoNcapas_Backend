package com.uca.ecommerce.repository;

import com.uca.ecommerce.domain.entities.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ReviewRepository extends JpaRepository<Review, UUID> {
    List<Review> findByProductId(UUID productId);
    List<Review> findByUserUuid(UUID userId);
    List<Review> findByProductSellerIdOrderByCreatedAtDesc(UUID sellerId);
    boolean existsByProductIdAndUserUuid(UUID productId, UUID userId);
}