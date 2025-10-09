package com.mss301.kraft.admin_service.controller;

import com.mss301.kraft.admin_service.dto.OrderAdminResponse;
import com.mss301.kraft.admin_service.service.OrderAdminService;
import com.mss301.kraft.order_service.dto.UpdateOrderStatusRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/orders")
@PreAuthorize("hasRole('ADMIN')")
public class OrderAdminController {

    private final OrderAdminService orderAdminService;

    public OrderAdminController(OrderAdminService orderAdminService) {
        this.orderAdminService = orderAdminService;
    }

    /**
     * Get all orders (admin only)
     */
    @GetMapping
    public ResponseEntity<List<OrderAdminResponse>> getAllOrders() {
        List<OrderAdminResponse> orders = orderAdminService.getAllOrders();
        return ResponseEntity.ok(orders);
    }

    /**
     * Get order by ID (admin only)
     */
    @GetMapping("/{orderId}")
    public ResponseEntity<OrderAdminResponse> getOrderById(@PathVariable UUID orderId) {
        OrderAdminResponse order = orderAdminService.getOrderById(orderId);
        return ResponseEntity.ok(order);
    }

    /**
     * Update order payment status (admin only)
     */
    @PutMapping("/{orderId}/payment-status")
    public ResponseEntity<OrderAdminResponse> updatePaymentStatus(
            @PathVariable UUID orderId,
            @RequestBody UpdateOrderStatusRequest request) {
        if (request.getPaymentStatus() == null) {
            throw new RuntimeException("Payment status is required");
        }
        OrderAdminResponse order = orderAdminService.updatePaymentStatus(orderId, request.getPaymentStatus());
        return ResponseEntity.ok(order);
    }

    /**
     * Update order status (admin only)
     */
    @PutMapping("/{orderId}/order-status")
    public ResponseEntity<OrderAdminResponse> updateOrderStatus(
            @PathVariable UUID orderId,
            @RequestBody UpdateOrderStatusRequest request) {
        if (request.getOrderStatus() == null) {
            throw new RuntimeException("Order status is required");
        }
        OrderAdminResponse order = orderAdminService.updateOrderStatus(orderId, request.getOrderStatus());
        return ResponseEntity.ok(order);
    }
}
