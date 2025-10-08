package com.mss301.kraft.coupon_service.dto;

import com.mss301.kraft.coupon_service.enums.CouponStatus;
import com.mss301.kraft.coupon_service.enums.CouponType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CouponResponse {
    
    private UUID id;
    private String code;
    private String name;
    private String description;
    private CouponType type;
    private BigDecimal value;
    private Map<String, Object> conditions;
    private Integer usageLimit;
    private Integer usedCount;
    private Integer usageLimitPerUser;
    private CouponStatus status;
    private LocalDateTime startsAt;
    private LocalDateTime expiresAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Boolean isActive;
    private Boolean isExpired;
}
