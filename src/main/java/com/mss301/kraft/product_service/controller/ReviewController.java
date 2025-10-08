package com.mss301.kraft.product_service.controller;

import com.mss301.kraft.product_service.dto.ReviewAdminRequest;
import com.mss301.kraft.product_service.dto.ReviewRequest;
import com.mss301.kraft.product_service.dto.ReviewResponse;
import com.mss301.kraft.product_service.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
@Tag(name = "Review", description = "Product review management APIs")
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Create a review (requires purchase verification)")
    public ResponseEntity<ReviewResponse> createReview(
            @Valid @RequestBody ReviewRequest request,
            Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());
        ReviewResponse response = reviewService.createReview(request, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/product/{productId}")
    @Operation(summary = "Get approved reviews for a product (public)")
    public ResponseEntity<List<ReviewResponse>> getProductReviews(@PathVariable UUID productId) {
        List<ReviewResponse> reviews = reviewService.getApprovedReviewsByProduct(productId);
        return ResponseEntity.ok(reviews);
    }

    @GetMapping("/product/{productId}/all")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get all reviews for a product including unapproved (Admin only)")
    public ResponseEntity<List<ReviewResponse>> getAllProductReviews(@PathVariable UUID productId) {
        List<ReviewResponse> reviews = reviewService.getAllReviewsByProduct(productId);
        return ResponseEntity.ok(reviews);
    }

    @GetMapping("/my-reviews")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get current user's reviews")
    public ResponseEntity<List<ReviewResponse>> getMyReviews(Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());
        List<ReviewResponse> reviews = reviewService.getUserReviews(userId);
        return ResponseEntity.ok(reviews);
    }

    @GetMapping("/pending")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get all pending reviews (Admin only)")
    public ResponseEntity<List<ReviewResponse>> getPendingReviews() {
        List<ReviewResponse> reviews = reviewService.getPendingReviews();
        return ResponseEntity.ok(reviews);
    }

    @GetMapping("/{reviewId}")
    @Operation(summary = "Get review by ID")
    public ResponseEntity<ReviewResponse> getReview(@PathVariable UUID reviewId) {
        ReviewResponse review = reviewService.getReviewById(reviewId);
        return ResponseEntity.ok(review);
    }

    @PutMapping("/{reviewId}/status")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update review status and add admin response (Admin only)")
    public ResponseEntity<ReviewResponse> updateReviewStatus(
            @PathVariable UUID reviewId,
            @Valid @RequestBody ReviewAdminRequest request) {
        ReviewResponse review = reviewService.updateReviewStatus(reviewId, request);
        return ResponseEntity.ok(review);
    }

    @DeleteMapping("/{reviewId}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Delete own review")
    public ResponseEntity<Void> deleteReview(
            @PathVariable UUID reviewId,
            Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());
        reviewService.deleteReview(reviewId, userId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{reviewId}/admin")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete any review (Admin only)")
    public ResponseEntity<Void> deleteReviewByAdmin(@PathVariable UUID reviewId) {
        reviewService.deleteReviewByAdmin(reviewId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/product/{productId}/stats")
    @Operation(summary = "Get product review statistics")
    public ResponseEntity<ReviewStats> getProductReviewStats(@PathVariable UUID productId) {
        Double avgRating = reviewService.getProductAverageRating(productId);
        Long count = reviewService.getProductReviewCount(productId);
        return ResponseEntity.ok(new ReviewStats(avgRating, count));
    }

    // Inner class for review statistics
    public record ReviewStats(Double averageRating, Long totalReviews) {}
}
