package com.mss301.kraft.admin_service.dto;

import com.mss301.kraft.coupon_service.dto.CouponResponse;
import lombok.Data;
import java.util.List;

@Data
public class CouponPageResponse {
    private List<CouponResponse> items;
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;
}
