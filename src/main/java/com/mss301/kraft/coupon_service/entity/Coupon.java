package com.mss301.kraft.coupon_service.entity;

import com.mss301.kraft.common.BaseEntity;
import com.mss301.kraft.coupon_service.enums.CouponStatus;
import com.mss301.kraft.coupon_service.enums.CouponType;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Entity
@Table(name = "coupons", indexes = {
    @Index(name = "idx_coupons_code", columnList = "code", unique = true),
    @Index(name = "idx_coupons_status", columnList = "status"),
    @Index(name = "idx_coupons_expires_at", columnList = "expires_at")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Coupon extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String code;

    @Column(nullable = false)
    private String name;

    @Column(length = 1000)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CouponType type;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal value;

    // Điều kiện áp dụng coupon (JSON string)
    // Ví dụ: {"minSpend": 500000, "maxDiscount": 100000, "productIds": [1,2,3], "categoryIds": [1,2]}
    @Column(columnDefinition = "TEXT")
    private String conditionsJson;

    @Column(name = "usage_limit")
    private Integer usageLimit; // Số lần sử dụng tối đa (null = unlimited)

    @Column(name = "used_count")
    @Builder.Default
    private Integer usedCount = 0;

    @Column(name = "usage_limit_per_user")
    private Integer usageLimitPerUser; // Số lần mỗi user có thể dùng

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private CouponStatus status = CouponStatus.ACTIVE;

    @Column(name = "starts_at")
    private OffsetDateTime startsAt;

    @Column(name = "expires_at")
    private OffsetDateTime expiresAt;

    @Column(name = "show_in_banner")
    @Builder.Default
    private Boolean showInBanner = false;

    public boolean isExpired() {
        if (expiresAt == null) {
            return false;
        }
        return OffsetDateTime.now().isAfter(expiresAt);
    }

    public boolean isActive() {
        if (status != CouponStatus.ACTIVE) {
            return false;
        }
        
        OffsetDateTime now = OffsetDateTime.now();
        
        if (startsAt != null && now.isBefore(startsAt)) {
            return false;
        }
        
        if (expiresAt != null && now.isAfter(expiresAt)) {
            return false;
        }
        
        if (usageLimit != null && usedCount >= usageLimit) {
            return false;
        }
        
        return true;
    }
}
