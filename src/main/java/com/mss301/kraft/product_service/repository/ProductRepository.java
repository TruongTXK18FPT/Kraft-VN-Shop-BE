package com.mss301.kraft.product_service.repository;

import com.mss301.kraft.product_service.entity.Product;
import com.mss301.kraft.product_service.enums.ProductStatus;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

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
            "AND ( :likeQuery IS NULL OR ( LOWER(p.name) LIKE :likeQuery " +
            "OR LOWER(p.description) LIKE :likeQuery " +
            "OR LOWER(p.brand) LIKE :likeQuery ) ) " +
            "AND ( :collectionIdsEmpty = TRUE OR p.collection.id IN (:collectionIds) ) " +
            "AND ( :categoryIdsEmpty = TRUE OR p.category.id IN (:categoryIds) )")
    List<Product> searchProducts(@Param("status") ProductStatus status,
            @Param("likeQuery") String likeQuery,
            @Param("collectionIds") List<UUID> collectionIds,
            @Param("categoryIds") List<UUID> categoryIds,
            @Param("collectionIdsEmpty") boolean collectionIdsEmpty,
            @Param("categoryIdsEmpty") boolean categoryIdsEmpty);

    @EntityGraph(attributePaths = { "variants", "collection", "category" })
    @Query(value = "SELECT DISTINCT p FROM Product p WHERE p.status = :status " +
            "AND ( :likeQuery IS NULL OR ( LOWER(p.name) LIKE :likeQuery " +
            "OR LOWER(p.description) LIKE :likeQuery " +
            "OR LOWER(p.brand) LIKE :likeQuery ) ) " +
            "AND ( :collectionIdsEmpty = TRUE OR p.collection.id IN (:collectionIds) ) " +
            "AND ( :categoryIdsEmpty = TRUE OR p.category.id IN (:categoryIds) )", countQuery = "SELECT COUNT(DISTINCT p) FROM Product p WHERE p.status = :status "
                    +
                    "AND ( :likeQuery IS NULL OR ( LOWER(p.name) LIKE :likeQuery " +
                    "OR LOWER(p.description) LIKE :likeQuery " +
                    "OR LOWER(p.brand) LIKE :likeQuery ) ) " +
                    "AND ( :collectionIdsEmpty = TRUE OR p.collection.id IN (:collectionIds) ) " +
                    "AND ( :categoryIdsEmpty = TRUE OR p.category.id IN (:categoryIds) )")
    Page<Product> searchProductsPaged(@Param("status") ProductStatus status,
            @Param("likeQuery") String likeQuery,
            @Param("collectionIds") List<UUID> collectionIds,
            @Param("categoryIds") List<UUID> categoryIds,
            @Param("collectionIdsEmpty") boolean collectionIdsEmpty,
            @Param("categoryIdsEmpty") boolean categoryIdsEmpty,
            Pageable pageable);

    // Facet counts: collections (respect current filters except collection facet
    // itself)
    @Query("SELECT p.collection.id as id, p.collection.name as name, COUNT(DISTINCT p.id) as cnt " +
            "FROM Product p WHERE p.status = :status " +
            "AND ( :likeQuery IS NULL OR ( LOWER(p.name) LIKE :likeQuery " +
            "OR LOWER(p.description) LIKE :likeQuery " +
            "OR LOWER(p.brand) LIKE :likeQuery ) ) " +
            "AND ( :categoryIdsEmpty = TRUE OR p.category.id IN (:categoryIds) ) " +
            "GROUP BY p.collection.id, p.collection.name")
    List<Object[]> facetCountsByCollection(@Param("status") ProductStatus status,
            @Param("likeQuery") String likeQuery,
            @Param("categoryIds") List<UUID> categoryIds,
            @Param("categoryIdsEmpty") boolean categoryIdsEmpty);

    // Facet counts: categories (respect current filters except category facet
    // itself)
    @Query("SELECT p.category.id as id, p.category.name as name, COUNT(DISTINCT p.id) as cnt " +
            "FROM Product p WHERE p.status = :status " +
            "AND ( :likeQuery IS NULL OR ( LOWER(p.name) LIKE :likeQuery " +
            "OR LOWER(p.description) LIKE :likeQuery " +
            "OR LOWER(p.brand) LIKE :likeQuery ) ) " +
            "AND ( :collectionIdsEmpty = TRUE OR p.collection.id IN (:collectionIds) ) " +
            "GROUP BY p.category.id, p.category.name")
    List<Object[]> facetCountsByCategory(@Param("status") ProductStatus status,
            @Param("likeQuery") String likeQuery,
            @Param("collectionIds") List<UUID> collectionIds,
            @Param("collectionIdsEmpty") boolean collectionIdsEmpty);

    // Dashboard analytics methods
    @Query("SELECT COUNT(p) FROM Product p WHERE p.createdAt < :date")
    Long countByCreatedAtBefore(@Param("date") OffsetDateTime date);
}
