package com.mss301.kraft.order_service.entity;

import com.mss301.kraft.common.BaseEntity;
import com.mss301.kraft.common.enums.OrderStatus;
import com.mss301.kraft.common.enums.PaymentStatus;
import com.mss301.kraft.user_service.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "orders", indexes = {
        @Index(name = "idx_orders_code", columnList = "code", unique = true),
        @Index(name = "idx_orders_user_id", columnList = "user_id")
})
public class Order extends BaseEntity {

    @Column(name = "code", nullable = false, unique = true)
    private String code;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private OrderStatus status = OrderStatus.PROCESSING;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status", nullable = false)
    private PaymentStatus paymentStatus = PaymentStatus.UNPAID;

    @Column(name = "shipping_fee")
    private BigDecimal shippingFee;

    @Column(name = "total")
    private BigDecimal total;

    // Recipient information (snapshot at order time, not linked to user table)
    @Column(name = "recipient_name")
    private String recipientName;

    @Column(name = "recipient_phone")
    private String recipientPhone;

    @Column(name = "recipient_address")
    private String recipientAddress;

    @Column(name = "recipient_ward")
    private String recipientWard;

    @Column(name = "recipient_district")
    private String recipientDistrict;

    @Column(name = "recipient_province")
    private String recipientProvince;

    @Column(name = "notes")
    private String notes; // Customer notes/special instructions

    @Column(name = "payment_method")
    private String paymentMethod; // e.g., "bank_transfer"

    @OneToMany(mappedBy = "order", fetch = FetchType.LAZY, cascade = jakarta.persistence.CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> items = new ArrayList<>();
}
