package com.mss301.kraft.product_service.repository;

import com.mss301.kraft.product_service.entity.Product;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, UUID> {

    @Override
    @EntityGraph(attributePaths = { "variants", "collection", "category" })
    List<Product> findAll();

    @EntityGraph(attributePaths = { "variants", "collection", "category" })
    @Query("SELECT p FROM Product p WHERE p.id = :id")
    java.util.Optional<Product> findByIdWithRelations(@Param("id") UUID id);
}
