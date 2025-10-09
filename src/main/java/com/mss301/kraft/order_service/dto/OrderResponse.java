package com.mss301.kraft.order_service.dto;

import com.mss301.kraft.common.enums.OrderStatus;
import com.mss301.kraft.common.enums.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponse {
    private UUID id;
    private String code;
    private UUID userId;
    private String userName;
    private String userEmail;
    private OrderStatus status;
    private PaymentStatus paymentStatus;
    private BigDecimal shippingFee;
    private BigDecimal total;

    // Recipient information
    private String recipientName;
    private String recipientPhone;
    private String recipientAddress;
    private String recipientWard;
    private String recipientDistrict;
    private String recipientProvince;

    private String notes;
    private String paymentMethod;

    private List<OrderItemResponse> items;

    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}
