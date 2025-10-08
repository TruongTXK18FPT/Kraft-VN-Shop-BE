package com.mss301.kraft.product_service.repository;

import com.mss301.kraft.product_service.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ReviewRepository extends JpaRepository<Review, UUID> {

    // Find all reviews for a product (approved only for public)
    @Query("SELECT r FROM Review r WHERE r.product.id = :productId AND r.approved = true ORDER BY r.createdAt DESC")
    List<Review> findApprovedByProductId(@Param("productId") UUID productId);

    // Find all reviews for a product (including unapproved for admin)
    @Query("SELECT r FROM Review r WHERE r.product.id = :productId ORDER BY r.createdAt DESC")
    List<Review> findAllByProductId(@Param("productId") UUID productId);

    // Find user's reviews
    @Query("SELECT r FROM Review r WHERE r.user.id = :userId ORDER BY r.createdAt DESC")
    List<Review> findByUserId(@Param("userId") UUID userId);

    // Check if user already reviewed this product
    @Query("SELECT r FROM Review r WHERE r.user.id = :userId AND r.product.id = :productId")
    Optional<Review> findByUserIdAndProductId(@Param("userId") UUID userId, @Param("productId") UUID productId);

    // Find all pending reviews (for admin)
    @Query("SELECT r FROM Review r WHERE r.approved = false ORDER BY r.createdAt ASC")
    List<Review> findPendingReviews();

    // Calculate average rating for product
    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.product.id = :productId AND r.approved = true")
    Double calculateAverageRating(@Param("productId") UUID productId);

    // Count approved reviews for product
    @Query("SELECT COUNT(r) FROM Review r WHERE r.product.id = :productId AND r.approved = true")
    Long countApprovedByProductId(@Param("productId") UUID productId);
}
