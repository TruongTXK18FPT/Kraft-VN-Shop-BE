package com.mss301.kraft.order_service.controller;

import com.mss301.kraft.order_service.dto.CreateOrderRequest;
import com.mss301.kraft.order_service.dto.OrderResponse;
import com.mss301.kraft.order_service.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    /**
     * Create a new order from the user's cart
     * User must be authenticated
     */
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<OrderResponse> createOrder(
            @RequestBody CreateOrderRequest request,
            Authentication authentication) {
        OrderResponse order = orderService.createOrderFromCart(authentication, request);
        return ResponseEntity.ok(order);
    }

    /**
     * Get all orders for the current user
     */
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<OrderResponse>> getMyOrders(Authentication authentication) {
        List<OrderResponse> orders = orderService.getUserOrders(authentication);
        return ResponseEntity.ok(orders);
    }

    /**
     * Get a specific order by code
     * User can only access their own orders
     */
    @GetMapping("/{code}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<OrderResponse> getOrderByCode(
            @PathVariable String code,
            Authentication authentication) {
        OrderResponse order = orderService.getOrderByCode(authentication, code);
        return ResponseEntity.ok(order);
    }
}
