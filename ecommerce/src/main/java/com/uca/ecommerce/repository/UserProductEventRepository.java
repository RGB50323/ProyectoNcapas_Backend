package com.uca.ecommerce.repository;

import com.uca.ecommerce.common.Enums.ProductEventType;
import com.uca.ecommerce.domain.entities.UserProductEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface UserProductEventRepository extends
        JpaRepository<UserProductEvent, UUID> {

    List<UserProductEvent> findByUser_UuidOrderByCreatedAtDesc(UUID userId);

    long countByProduct_Id(UUID productId);

    boolean existsByUser_UuidAndProduct_IdAndTypeAndCreatedAtAfter(
            UUID userId,
            UUID productId,
            ProductEventType type,
            LocalDateTime createdAt
    );
}
