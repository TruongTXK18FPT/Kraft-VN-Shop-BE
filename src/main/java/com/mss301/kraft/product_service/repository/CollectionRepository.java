package com.mss301.kraft.product_service.repository;

import com.mss301.kraft.product_service.entity.Collection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CollectionRepository extends JpaRepository<Collection, UUID> {
    Optional<Collection> findBySlug(String slug);
    boolean existsByName(String name);
}
