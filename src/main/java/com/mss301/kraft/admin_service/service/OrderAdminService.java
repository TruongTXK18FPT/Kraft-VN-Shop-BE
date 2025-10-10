package com.mss301.kraft.admin_service.service;

import com.mss301.kraft.admin_service.dto.OrderAdminResponse;
import com.mss301.kraft.admin_service.dto.OrderAdminPageResponse;
import com.mss301.kraft.common.enums.OrderStatus;
import com.mss301.kraft.common.enums.PaymentStatus;
import com.mss301.kraft.order_service.dto.OrderItemResponse;
import com.mss301.kraft.order_service.entity.Order;
import com.mss301.kraft.order_service.entity.OrderItem;
import com.mss301.kraft.order_service.repository.OrderRepository;
import com.mss301.kraft.product_service.entity.ProductVariant;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class OrderAdminService {

    private final OrderRepository orderRepository;

    public OrderAdminService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    /**
     * Get all orders (admin only)
     */
    public List<OrderAdminResponse> getAllOrders() {
        List<Order> orders = orderRepository.findAllOrders();
        return orders.stream().map(this::toOrderAdminResponse).collect(Collectors.toList());
    }

    public OrderAdminPageResponse getAllOrdersPaged(int page, int size) {
        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(page, size);
        org.springframework.data.domain.Page<Order> result = orderRepository.findAllOrdersPaged(pageable);
        OrderAdminPageResponse resp = new OrderAdminPageResponse();
        resp.setItems(result.getContent().stream().map(this::toOrderAdminResponse).collect(Collectors.toList()));
        resp.setPage(result.getNumber());
        resp.setSize(result.getSize());
        resp.setTotalElements(result.getTotalElements());
        resp.setTotalPages(result.getTotalPages());
        return resp;
    }

    /**
     * Get order by ID (admin only)
     */
    public OrderAdminResponse getOrderById(UUID orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        return toOrderAdminResponse(order);
    }

    /**
     * Update payment status (admin only)
     */
    @Transactional
    public OrderAdminResponse updatePaymentStatus(UUID orderId, PaymentStatus paymentStatus) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        order.setPaymentStatus(paymentStatus);

        // Auto-update order status when payment is confirmed
        if (paymentStatus == PaymentStatus.PAID && order.getStatus() == OrderStatus.PROCESSING) {
            order.setStatus(OrderStatus.PAID);
        }

        Order savedOrder = orderRepository.save(order);
        return toOrderAdminResponse(savedOrder);
    }

    /**
     * Update order status (admin only)
     */
    @Transactional
    public OrderAdminResponse updateOrderStatus(UUID orderId, OrderStatus orderStatus) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        order.setStatus(orderStatus);
        Order savedOrder = orderRepository.save(order);
        return toOrderAdminResponse(savedOrder);
    }

    /**
     * Convert Order entity to OrderAdminResponse DTO
     */
    private OrderAdminResponse toOrderAdminResponse(Order order) {
        OrderAdminResponse response = new OrderAdminResponse();
        response.setId(order.getId());
        response.setCode(order.getCode());
        response.setUserId(order.getUser().getId());
        response.setUserName(order.getUser().getName());
        response.setUserEmail(order.getUser().getEmail());
        response.setUserPhone(order.getUser().getPhone());
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
}
