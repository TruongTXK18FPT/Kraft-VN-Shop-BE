package com.mss301.kraft.product_service.repository;

import com.mss301.kraft.product_service.entity.Product;
import com.mss301.kraft.product_service.enums.ProductStatus;
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

    @EntityGraph(attributePaths = { "variants", "collection", "category" })
    List<Product> findAllByStatus(ProductStatus status);

    @EntityGraph(attributePaths = { "variants", "collection", "category" })
    List<Product> findAllByFeaturedTrueAndStatus(ProductStatus status);

    @EntityGraph(attributePaths = { "variants", "collection", "category" })
    @Query("SELECT DISTINCT p FROM Product p JOIN p.variants v WHERE p.status = :status AND v.salePrice IS NOT NULL")
    List<Product> findAllOnSale(@Param("status") ProductStatus status);

    @EntityGraph(attributePaths = { "variants", "collection", "category" })
    java.util.Optional<Product> findBySlug(String slug);
}
