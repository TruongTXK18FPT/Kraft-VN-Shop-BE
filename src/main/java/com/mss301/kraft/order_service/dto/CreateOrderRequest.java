package com.mss301.kraft.order_service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateOrderRequest {
    private String recipientName;
    private String recipientPhone;
    private String recipientAddress;
    private String recipientWard;
    private String recipientDistrict;
    private String recipientProvince;
    private String notes;
    private String paymentMethod; // Should be "bank_transfer"
    private BigDecimal shippingFee;
    private String couponCode; // Optional coupon code
}
