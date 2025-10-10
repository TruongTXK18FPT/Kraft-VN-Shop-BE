package com.mss301.kraft.product_service.service;

import com.mss301.kraft.common.enums.OrderStatus;
import com.mss301.kraft.common.exception.BadRequestException;
import com.mss301.kraft.common.exception.ResourceNotFoundException;
import com.mss301.kraft.order_service.entity.Order;
import com.mss301.kraft.order_service.repository.OrderRepository;
import com.mss301.kraft.product_service.dto.ReviewAdminRequest;
import com.mss301.kraft.product_service.dto.ReviewRequest;
import com.mss301.kraft.product_service.dto.ReviewResponse;
import com.mss301.kraft.product_service.entity.Product;
import com.mss301.kraft.product_service.entity.Review;
import com.mss301.kraft.product_service.repository.ProductRepository;
import com.mss301.kraft.product_service.repository.ProductVariantRepository;
import com.mss301.kraft.product_service.repository.ReviewRepository;
import com.mss301.kraft.user_service.entity.User;
import com.mss301.kraft.user_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ProductRepository productRepository;
    private final ProductVariantRepository productVariantRepository;
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;

    @Transactional
    public ReviewResponse createReview(ReviewRequest request, UUID userId) {
        try {
            System.out.println("=== REVIEW CREATION DEBUG ===");
            System.out.println("Creating review for user: " + userId + ", product: " + request.getProductId());
            
            // Debug: Check if repositories are working
            System.out.println("Testing repositories...");
            System.out.println("ProductRepository count: " + productRepository.count());
            System.out.println("ProductVariantRepository count: " + productVariantRepository.count());
            
            // Debug: Check if the ID is valid
            System.out.println("Product ID is valid UUID: " + request.getProductId());
            
            // Check if user exists
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found"));
            System.out.println("User found: " + user.getName());

            // Try to find product by ID first, if not found, try to find by variant ID
            Product product = null;
            System.out.println("Step 1: Trying to find product by ID: " + request.getProductId());
            try {
                product = productRepository.findById(request.getProductId())
                        .orElse(null);
                if (product != null) {
                    System.out.println("SUCCESS: Product found by ID: " + product.getName());
                } else {
                    System.out.println("Product not found by ID, trying variant ID...");
                }
            } catch (Exception e) {
                System.out.println("ERROR: Product not found by ID, trying variant ID: " + e.getMessage());
            }
            
            // If product not found by ID, try to find by variant ID
            if (product == null) {
                System.out.println("Step 2: Trying to find product by variant ID: " + request.getProductId());
                try {
                    var variant = productVariantRepository.findById(request.getProductId()).orElse(null);
                    System.out.println("Variant lookup result: " + (variant != null ? "Found" : "Not found"));
                    if (variant != null) {
                        System.out.println("Variant product: " + (variant.getProduct() != null ? "Has product" : "No product"));
                        if (variant.getProduct() != null) {
                            product = variant.getProduct();
                            System.out.println("SUCCESS: Product found by variant ID: " + product.getName());
                        }
                    }
                } catch (Exception e) {
                    System.out.println("ERROR: Product not found by variant ID: " + e.getMessage());
                    e.printStackTrace();
                }
            }
            
            if (product == null) {
                System.out.println("ERROR: Product not found with ID: " + request.getProductId());
                throw new ResourceNotFoundException("Product not found with ID: " + request.getProductId());
            }

            // Check if user already reviewed this product
            reviewRepository.findByUserIdAndProductId(userId, product.getId())
                    .ifPresent(r -> {
                        throw new BadRequestException("You have already reviewed this product");
                    });

            // Skip purchase verification for now to test
            boolean hasPurchased = false; // verifyPurchase(userId, product.getId());
            System.out.println("Purchase verified: " + hasPurchased);

            // Create review
            Review review = new Review();
            review.setUser(user);
            review.setProduct(product);
            review.setRating(request.getRating());
            review.setContent(request.getContent());
            review.setPurchaseVerified(hasPurchased);
            review.setApproved(false); // Needs admin approval

            System.out.println("Saving review...");
            Review savedReview = reviewRepository.save(review);
            System.out.println("Review saved with ID: " + savedReview.getId());

            return mapToResponse(savedReview);
        } catch (Exception e) {
            System.err.println("Error creating review: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to create review: " + e.getMessage(), e);
        }
    }

    public List<ReviewResponse> getApprovedReviewsByProduct(UUID productId) {
        return reviewRepository.findApprovedByProductId(productId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<ReviewResponse> getAllReviewsByProduct(UUID productId) {
        return reviewRepository.findAllByProductId(productId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<ReviewResponse> getUserReviews(UUID userId) {
        return reviewRepository.findByUserId(userId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<ReviewResponse> getPendingReviews() {
        return reviewRepository.findPendingReviews()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public ReviewResponse getReviewById(UUID reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found"));
        return mapToResponse(review);
    }

    @Transactional
    public ReviewResponse updateReviewStatus(UUID reviewId, ReviewAdminRequest request) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found"));

        review.setApproved(request.isApproved());
        if (request.getAdminResponse() != null) {
            review.setAdminResponse(request.getAdminResponse());
        }

        Review updatedReview = reviewRepository.save(review);
        return mapToResponse(updatedReview);
    }

    @Transactional
    public void deleteReview(UUID reviewId, UUID userId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found"));

        // Check if user owns this review
        if (!review.getUser().getId().equals(userId)) {
            throw new BadRequestException("You can only delete your own reviews");
        }

        reviewRepository.delete(review);
    }

    @Transactional
    public void deleteReviewByAdmin(UUID reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found"));
        reviewRepository.delete(review);
    }

    public Double getProductAverageRating(UUID productId) {
        Double average = reviewRepository.calculateAverageRating(productId);
        return average != null ? Math.round(average * 10.0) / 10.0 : 0.0;
    }

    public Long getProductReviewCount(UUID productId) {
        return reviewRepository.countApprovedByProductId(productId);
    }

    /**
     * Verify if user has purchased the product
     * Checks if user has any completed order containing this product
     */
    private boolean verifyPurchase(UUID userId, UUID productId) {
        try {
            List<Order> userOrders = orderRepository.findByUserId(userId);
            
            if (userOrders == null || userOrders.isEmpty()) {
                return false;
            }

            for (Order order : userOrders) {
                // Only check completed/shipping orders
                if (order.getStatus() == OrderStatus.COMPLETED ||
                        order.getStatus() == OrderStatus.SHIPPING) {

                    // Check if this order contains the product
                    if (order.getItems() != null) {
                        boolean hasProduct = order.getItems()
                                .stream()
                                .anyMatch(item -> {
                                    try {
                                        return item.getProductVariant() != null &&
                                                item.getProductVariant().getProduct() != null &&
                                                item.getProductVariant().getProduct().getId().equals(productId);
                                    } catch (Exception e) {
                                        System.err.println("Error checking product variant: " + e.getMessage());
                                        return false;
                                    }
                                });

                        if (hasProduct) {
                            return true;
                        }
                    }
                }
            }

            return false;
        } catch (Exception e) {
            System.err.println("Error verifying purchase: " + e.getMessage());
            e.printStackTrace();
            // Return false if verification fails
            return false;
        }
    }

    private ReviewResponse mapToResponse(Review review) {
        try {
            return ReviewResponse.builder()
                    .id(review.getId())
                    .productId(review.getProduct() != null ? review.getProduct().getId() : null)
                    .productName(review.getProduct() != null ? review.getProduct().getName() : "Unknown Product")
                    .userId(review.getUser() != null ? review.getUser().getId() : null)
                    .userName(review.getUser() != null ? review.getUser().getName() : "Unknown User")
                    .rating(review.getRating())
                    .content(review.getContent())
                    .approved(review.isApproved())
                    .purchaseVerified(review.isPurchaseVerified())
                    .adminResponse(review.getAdminResponse())
                    .createdAt(review.getCreatedAt())
                    .updatedAt(review.getUpdatedAt())
                    .build();
        } catch (Exception e) {
            System.err.println("Error mapping review to response: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to map review to response: " + e.getMessage(), e);
        }
    }
}
