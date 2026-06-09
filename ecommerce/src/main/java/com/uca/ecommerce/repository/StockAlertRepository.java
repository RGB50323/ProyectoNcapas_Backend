package com.uca.ecommerce.repository;

import com.uca.ecommerce.domain.entities.StockAlert;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface StockAlertRepository extends JpaRepository<StockAlert, UUID> {

    List<StockAlert> findByUserUuid(UUID userUuid);

    List<StockAlert> findByProductId(UUID productId);

    @Query("SELECT CASE WHEN COUNT(s) > 0 THEN true ELSE false END FROM StockAlert s WHERE s.user.uuid = :userUuid AND s.product.id = :productId")
    boolean existsByUserUuidAndProductId(UUID userUuid, UUID productId);
}