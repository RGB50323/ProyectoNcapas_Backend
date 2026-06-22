package com.uca.ecommerce.repository;

import com.uca.ecommerce.common.Enums.StoreRequestStatus;
import com.uca.ecommerce.domain.entities.StoreRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface StoreRequestRepository extends JpaRepository<StoreRequest, UUID> {
    Optional<StoreRequest> findFirstByUserUuidOrderByCreatedAtDesc(UUID userId);
    boolean existsByUserUuidAndStatus(UUID userId, StoreRequestStatus status);
    List<StoreRequest> findAllByOrderByCreatedAtDesc();
    List<StoreRequest> findByStatusOrderByCreatedAtAsc(StoreRequestStatus status);
}
