package com.mss301.kraft.coupon_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CouponValidationResponse {
    
    private Boolean valid;
    private String message;
    private UUID couponId;
    private BigDecimal discountAmount;
    private BigDecimal finalAmount;
}
