package com.mss301.kraft.order_service.service;

import com.mss301.kraft.cart_service.entity.Cart;
import com.mss301.kraft.cart_service.entity.CartItem;
import com.mss301.kraft.cart_service.repository.CartRepository;
import com.mss301.kraft.common.enums.OrderStatus;
import com.mss301.kraft.common.enums.PaymentStatus;
import com.mss301.kraft.coupon_service.service.CouponService;
import com.mss301.kraft.coupon_service.dto.ValidateCouponRequest;
import com.mss301.kraft.coupon_service.dto.CouponValidationResponse;
import com.mss301.kraft.order_service.dto.CreateOrderRequest;
import com.mss301.kraft.order_service.dto.OrderItemResponse;
import com.mss301.kraft.order_service.dto.OrderResponse;
import com.mss301.kraft.order_service.dto.CancelOrderRequest;
import com.mss301.kraft.order_service.entity.Order;
import com.mss301.kraft.order_service.entity.OrderItem;
import com.mss301.kraft.order_service.repository.OrderRepository;
import com.mss301.kraft.product_service.entity.ProductVariant;
import com.mss301.kraft.user_service.entity.User;
import com.mss301.kraft.user_service.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final UserRepository userRepository;
    private final CouponService couponService;
    private final SecureRandom random = new SecureRandom();

    public OrderService(OrderRepository orderRepository,
            CartRepository cartRepository,
            UserRepository userRepository,
            CouponService couponService) {
        this.orderRepository = orderRepository;
        this.cartRepository = cartRepository;
        this.userRepository = userRepository;
        this.couponService = couponService;
    }

    /**
     * Generate a unique order code in format: ORD-{8 chars}
     * e.g., ORD-asd123xy
     */
    private String generateUniqueOrderCode() {
        String chars = "abcdefghijklmnopqrstuvwxyz0123456789";
        int maxAttempts = 10;

        for (int attempt = 0; attempt < maxAttempts; attempt++) {
            StringBuilder code = new StringBuilder("ORD-");
            for (int i = 0; i < 8; i++) {
                code.append(chars.charAt(random.nextInt(chars.length())));
            }
            String orderCode = code.toString();

            // Check if this code already exists
            if (orderRepository.findByCode(orderCode).isEmpty()) {
                return orderCode;
            }
        }

        // Fallback: use timestamp + random
        return "ORD-" + System.currentTimeMillis() % 100000000;
    }

    /**
     * Create an order from the user's cart
     * Transaction ensures atomicity: order + items created together, cart cleared
     */
    @Transactional
    public OrderResponse createOrderFromCart(Authentication authentication, CreateOrderRequest request) {
        // Get current user
        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Get user's cart (get the most recent one if multiple exist)
        List<Cart> carts = cartRepository.findByUser(user);
        if (carts.isEmpty()) {
            throw new RuntimeException("Cart not found");
        }
        Cart cart = carts.get(0); // Get the most recent cart

        // Validate cart has items
        if (cart.getItems() == null || cart.getItems().isEmpty()) {
            throw new RuntimeException("Cart is empty");
        }

        // DEBUG: Log cart items count
        System.out.println("DEBUG: Cart ID: " + cart.getId() + ", Items count: " + cart.getItems().size());
        for (CartItem item : cart.getItems()) {
            System.out.println("DEBUG: Cart Item ID: " + item.getId() + ", Product: " +
                    (item.getProductVariant() != null && item.getProductVariant().getProduct() != null
                            ? item.getProductVariant().getProduct().getName()
                            : "Unknown")
                    +
                    ", Qty: " + item.getQty());
        }

        // Create order
        Order order = new Order();
        order.setCode(generateUniqueOrderCode());
        order.setUser(user);
        order.setStatus(OrderStatus.PROCESSING);
        order.setPaymentStatus(PaymentStatus.UNPAID);
        order.setShippingFee(request.getShippingFee() != null ? request.getShippingFee() : BigDecimal.ZERO);

        // Set recipient information (snapshot from request)
        order.setRecipientName(request.getRecipientName());
        order.setRecipientPhone(request.getRecipientPhone());
        order.setRecipientAddress(request.getRecipientAddress());
        order.setRecipientWard(request.getRecipientWard());
        order.setRecipientDistrict(request.getRecipientDistrict());
        order.setRecipientProvince(request.getRecipientProvince());
        order.setNotes(request.getNotes());
        order.setPaymentMethod(request.getPaymentMethod());

        // Validate stock availability and calculate total
        BigDecimal itemsTotal = BigDecimal.ZERO;
        List<OrderItem> orderItems = new ArrayList<>();

        System.out.println("DEBUG: Starting to process " + cart.getItems().size() + " cart items");

        for (CartItem cartItem : cart.getItems()) {
            ProductVariant variant = cartItem.getProductVariant();
            System.out.println("DEBUG: Processing cart item: " + cartItem.getId());

            // Check if sufficient stock is available
            if (variant.getStock() == null || variant.getStock() < cartItem.getQty()) {
                throw new RuntimeException("Insufficient stock for product: " +
                        (variant.getProduct() != null ? variant.getProduct().getName() : "Unknown") +
                        " (SKU: " + variant.getSku() + "). Available: " +
                        (variant.getStock() != null ? variant.getStock() : 0) +
                        ", Requested: " + cartItem.getQty());
            }

            // Create order item
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProductVariant(variant);
            orderItem.setQty(cartItem.getQty());
            orderItem.setPrice(cartItem.getPrice());

            BigDecimal lineTotal = cartItem.getPrice().multiply(BigDecimal.valueOf(cartItem.getQty()));
            itemsTotal = itemsTotal.add(lineTotal);

            // Deduct stock from product variant
            variant.setStock(variant.getStock() - cartItem.getQty());

            orderItems.add(orderItem);
            System.out.println("DEBUG: Added order item, total order items now: " + orderItems.size());
        }

        System.out.println("DEBUG: Finished processing all items. Total order items: " + orderItems.size());

        // Process coupon if provided - Single validation and application
        BigDecimal discountAmount = BigDecimal.ZERO;
        String couponCode = null;
        com.mss301.kraft.coupon_service.entity.Coupon coupon = null;
        if (request.getCouponCode() != null && !request.getCouponCode().trim().isEmpty()) {
            try {
                System.out.println("DEBUG: Validating coupon - Code: " + request.getCouponCode() +
                        ", UserId: " + user.getId() +
                        ", OrderTotal: " + itemsTotal);
                ValidateCouponRequest validateRequest = ValidateCouponRequest.builder()
                        .couponCode(request.getCouponCode())
                        .userId(user.getId())
                        .orderTotal(itemsTotal)
                        .build();

                CouponValidationResponse validation = couponService.validateCoupon(validateRequest);
                System.out.println("DEBUG: Coupon validation result - Valid: " + validation.getValid() +
                        ", DiscountAmount: " + validation.getDiscountAmount() +
                        ", Message: " + validation.getMessage());
                if (validation.getValid()) {
                    discountAmount = validation.getDiscountAmount();
                    couponCode = request.getCouponCode();
                    // Get the coupon entity by ID
                    coupon = couponService.getCouponEntityById(validation.getCouponId());
                    System.out.println("DEBUG: Coupon validated successfully - CouponId: " + coupon.getId() +
                            ", DiscountAmount: " + discountAmount);
                } else {
                    throw new RuntimeException("Coupon validation failed: " + validation.getMessage());
                }
            } catch (Exception e) {
                System.err.println("DEBUG: Coupon validation error: " + e.getMessage());
                e.printStackTrace();
                throw new RuntimeException("Invalid coupon: " + e.getMessage());
            }
        } else {
            System.out.println("DEBUG: No coupon code provided in request");
        }

        // Set total = items total + shipping fee - discount
        BigDecimal finalTotal = itemsTotal.add(order.getShippingFee()).subtract(discountAmount);
        order.setTotal(finalTotal);
        order.setCoupon(coupon);
        order.setCouponCode(couponCode);
        order.setDiscountAmount(discountAmount);
        order.setItems(orderItems);

        System.out.println("DEBUG: Set order items, count: " + order.getItems().size());

        // Save order (cascade will save order items)
        Order savedOrder = orderRepository.save(order);

        // Apply coupon usage after order is created with the actual order ID
        if (coupon != null && discountAmount.compareTo(BigDecimal.ZERO) > 0) {
            try {
                System.out.println("DEBUG: Applying coupon - CouponId: " + coupon.getId() +
                        ", UserId: " + user.getId() +
                        ", OrderId: " + savedOrder.getId() +
                        ", DiscountAmount: " + discountAmount);
                couponService.applyCoupon(coupon.getId(), user.getId(), savedOrder.getId(), discountAmount);
                System.out.println("DEBUG: Coupon application completed successfully");
            } catch (Exception e) {
                System.err.println("DEBUG: Coupon application failed: " + e.getMessage());
                e.printStackTrace();
                // This is critical - if we can't apply coupon usage, we should rollback
                throw new RuntimeException("Failed to apply coupon usage: " + e.getMessage());
            }
        } else {
            System.out.println("DEBUG: No coupon to apply - Coupon: " + coupon +
                    ", DiscountAmount: " + discountAmount);
        }

        // Clear cart items after successful order creation
        cart.getItems().clear();
        cart.setTotal(BigDecimal.ZERO);
        cartRepository.save(cart);

        // Convert to response
        return toOrderResponse(savedOrder);
    }

    /**
     * Get all orders for the current user
     */
    public List<OrderResponse> getUserOrders(Authentication authentication) {
        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Order> orders = orderRepository.findByUserId(user.getId());
        return orders.stream()
                .map(this::toOrderResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get a specific order by code (only if owned by current user)
     */
    public OrderResponse getOrderByCode(Authentication authentication, String code) {
        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Order order = orderRepository.findByCode(code)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        // Verify order belongs to current user
        if (!order.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Access denied");
        }

        return toOrderResponse(order);
    }

    /**
     * Convert Order entity to OrderResponse DTO
     */
    private OrderResponse toOrderResponse(Order order) {
        OrderResponse response = new OrderResponse();
        response.setId(order.getId());
        response.setCode(order.getCode());
        response.setUserId(order.getUser().getId());
        response.setUserName(order.getUser().getName());
        response.setUserEmail(order.getUser().getEmail());
        response.setStatus(order.getStatus());
        response.setPaymentStatus(order.getPaymentStatus());
        response.setShippingFee(order.getShippingFee());
        response.setTotal(order.getTotal());

        response.setRecipientName(order.getRecipientName());
        response.setRecipientPhone(order.getRecipientPhone());
        response.setRecipientAddress(order.getRecipientAddress());
        response.setRecipientWard(order.getRecipientWard());
        response.setRecipientDistrict(order.getRecipientDistrict());
        response.setRecipientProvince(order.getRecipientProvince());

        response.setNotes(order.getNotes());
        response.setPaymentMethod(order.getPaymentMethod());

        // Coupon information
        response.setCouponId(order.getCoupon() != null ? order.getCoupon().getId() : null);
        response.setCouponCode(order.getCouponCode());
        response.setDiscountAmount(order.getDiscountAmount());

        response.setCreatedAt(order.getCreatedAt());
        response.setUpdatedAt(order.getUpdatedAt());

        // Convert order items
        List<OrderItemResponse> itemResponses = order.getItems().stream()
                .map(this::toOrderItemResponse)
                .collect(Collectors.toList());
        response.setItems(itemResponses);

        return response;
    }

    /**
     * Convert OrderItem entity to OrderItemResponse DTO
     */
    private OrderItemResponse toOrderItemResponse(OrderItem item) {
        ProductVariant variant = item.getProductVariant();

        OrderItemResponse response = new OrderItemResponse();
        response.setId(item.getId());
        response.setVariantId(variant.getId());
        response.setProductName(variant.getProduct() != null ? variant.getProduct().getName() : "Unknown Product");
        response.setSku(variant.getSku());
        response.setColor(variant.getColor());
        response.setSize(variant.getSize());
        response.setImageUrl(variant.getImageUrl());
        response.setQty(item.getQty());
        response.setPrice(item.getPrice());
        response.setLineTotal(item.getPrice().multiply(BigDecimal.valueOf(item.getQty())));

        return response;
    }

    /**
     * Cancel an order
     * Only allows cancellation of COD orders in PROCESSING status
     */
    @Transactional
    public OrderResponse cancelOrder(Authentication authentication, String orderId, CancelOrderRequest request) {
        // Get current user
        String username = authentication.getName();
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));

        // Find the order
        Order order = orderRepository.findById(UUID.fromString(orderId))
                .orElseThrow(() -> new RuntimeException("Order not found"));

        // Check if user owns the order
        if (!order.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Access denied: You can only cancel your own orders");
        }

        // Check if order can be cancelled
        if (!canCancelOrder(order)) {
            throw new RuntimeException(
                    "Order cannot be cancelled. Only COD orders in PROCESSING status can be cancelled.");
        }

        // Update order status to CANCELED
        order.setStatus(OrderStatus.CANCELED);
        // Note: Cancellation reason is not stored in the current entity

        Order savedOrder = orderRepository.save(order);
        return toOrderResponse(savedOrder);
    }

    /**
     * Check if an order can be cancelled
     */
    private boolean canCancelOrder(Order order) {
        // Only COD orders in PROCESSING status can be cancelled
        return "cod".equalsIgnoreCase(order.getPaymentMethod()) &&
                order.getStatus() == OrderStatus.PROCESSING;
    }
}
