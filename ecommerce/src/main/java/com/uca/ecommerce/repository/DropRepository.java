package com.uca.ecommerce.repository;

import com.uca.ecommerce.domain.entities.Drop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface DropRepository extends JpaRepository<Drop, UUID> {
    boolean existsBySlug(String slug);
    List<Drop> findByOwner_Id(UUID ownerId);
}
