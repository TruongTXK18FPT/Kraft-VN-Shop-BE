package com.mss301.kraft.product_service.service;

import com.mss301.kraft.common.enums.OrderStatus;
import com.mss301.kraft.common.exception.BadRequestException;
import com.mss301.kraft.common.exception.ResourceNotFoundException;
import com.mss301.kraft.order_service.entity.Order;
import com.mss301.kraft.user_service.repository.OrderRepository;
import com.mss301.kraft.product_service.dto.ReviewAdminRequest;
import com.mss301.kraft.product_service.dto.ReviewRequest;
import com.mss301.kraft.product_service.dto.ReviewResponse;
import com.mss301.kraft.product_service.entity.Product;
import com.mss301.kraft.product_service.entity.Review;
import com.mss301.kraft.product_service.repository.ProductRepository;
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
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;

    @Transactional
    public ReviewResponse createReview(ReviewRequest request, UUID userId) {
        // Check if user exists
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Check if product exists
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        // Check if user already reviewed this product
        reviewRepository.findByUserIdAndProductId(userId, request.getProductId())
                .ifPresent(r -> {
                    throw new BadRequestException("You have already reviewed this product");
                });

        // Verify purchase: check if user has bought this product
        boolean hasPurchased = verifyPurchase(userId, request.getProductId());

        // Create review
        Review review = new Review();
        review.setUser(user);
        review.setProduct(product);
        review.setRating(request.getRating());
        review.setContent(request.getContent());
        review.setPurchaseVerified(hasPurchased);
        review.setApproved(false); // Needs admin approval

        Review savedReview = reviewRepository.save(review);

        return mapToResponse(savedReview);
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
        List<Order> userOrders = orderRepository.findByUserId(userId);

        for (Order order : userOrders) {
            // Only check completed/shipping orders
            if (order.getStatus() == OrderStatus.COMPLETED || 
                order.getStatus() == OrderStatus.SHIPPING) {
                
                // Check if this order contains the product
                boolean hasProduct = order.getItems()
                        .stream()
                        .anyMatch(item -> item.getProductVariant() != null && 
                                item.getProductVariant().getProduct() != null &&
                                item.getProductVariant().getProduct().getId().equals(productId));
                
                if (hasProduct) {
                    return true;
                }
            }
        }

        return false;
    }

    private ReviewResponse mapToResponse(Review review) {
        return ReviewResponse.builder()
                .id(review.getId())
                .productId(review.getProduct().getId())
                .productName(review.getProduct().getName())
                .userId(review.getUser().getId())
                .userName(review.getUser().getName())
                .rating(review.getRating())
                .content(review.getContent())
                .approved(review.isApproved())
                .purchaseVerified(review.isPurchaseVerified())
                .adminResponse(review.getAdminResponse())
                .createdAt(review.getCreatedAt())
                .updatedAt(review.getUpdatedAt())
                .build();
    }
}
