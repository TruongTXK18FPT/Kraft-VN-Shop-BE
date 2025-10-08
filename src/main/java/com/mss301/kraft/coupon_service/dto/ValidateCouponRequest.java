package com.mss301.kraft.coupon_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ValidateCouponRequest {
    
    @NotBlank(message = "Mã coupon không được để trống")
    private String couponCode;
    
    @NotNull(message = "User ID không được để trống")
    private UUID userId;
    
    @NotNull(message = "Tổng giá trị đơn hàng không được để trống")
    private BigDecimal orderTotal;
    
    private List<UUID> productIds;
    
    private List<UUID> categoryIds;
}
