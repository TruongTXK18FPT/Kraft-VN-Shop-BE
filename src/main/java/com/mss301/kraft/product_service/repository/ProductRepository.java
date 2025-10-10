package com.mss301.kraft.product_service.repository;

import com.mss301.kraft.product_service.entity.Product;
import com.mss301.kraft.product_service.enums.ProductStatus;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.OffsetDateTime;
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

    @EntityGraph(attributePaths = { "variants", "collection", "category" })
    @Query("SELECT DISTINCT p FROM Product p WHERE p.status = :status " +
           "AND (:query IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :query, '%')) " +
           "OR LOWER(p.description) LIKE LOWER(CONCAT('%', :query, '%')) " +
           "OR LOWER(p.brand) LIKE LOWER(CONCAT('%', :query, '%'))) " +
           "AND (:collectionId IS NULL OR p.collection.id = :collectionId) " +
           "AND (:categoryId IS NULL OR p.category.id = :categoryId)")
    List<Product> searchProducts(@Param("status") ProductStatus status,
                                @Param("query") String query,
                                @Param("collectionId") UUID collectionId,
                                @Param("categoryId") UUID categoryId);
    
    // Dashboard analytics methods
    @Query("SELECT COUNT(p) FROM Product p WHERE p.createdAt < :date")
    Long countByCreatedAtBefore(@Param("date") OffsetDateTime date);
}
