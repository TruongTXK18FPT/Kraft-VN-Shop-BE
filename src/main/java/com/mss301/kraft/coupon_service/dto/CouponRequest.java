package com.mss301.kraft.coupon_service.dto;

import com.mss301.kraft.coupon_service.enums.CouponStatus;
import com.mss301.kraft.coupon_service.enums.CouponType;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CouponRequest {
    
    @NotBlank(message = "Mã coupon không được để trống")
    @Pattern(regexp = "^[A-Z0-9_-]+$", message = "Mã coupon chỉ chứa chữ in hoa, số, gạch dưới và gạch ngang")
    private String code;
    
    @NotBlank(message = "Tên coupon không được để trống")
    private String name;
    
    private String description;
    
    @NotNull(message = "Loại coupon không được để trống")
    private CouponType type;
    
    @NotNull(message = "Giá trị coupon không được để trống")
    @DecimalMin(value = "0.0", inclusive = false, message = "Giá trị phải lớn hơn 0")
    private BigDecimal value;
    
    private Map<String, Object> conditions;
    
    @Min(value = 1, message = "Giới hạn sử dụng phải lớn hơn 0")
    private Integer usageLimit;
    
    @Min(value = 1, message = "Giới hạn sử dụng per user phải lớn hơn 0")
    private Integer usageLimitPerUser;
    
    private CouponStatus status;
    
    private OffsetDateTime startsAt;
    
    private OffsetDateTime expiresAt;
}
