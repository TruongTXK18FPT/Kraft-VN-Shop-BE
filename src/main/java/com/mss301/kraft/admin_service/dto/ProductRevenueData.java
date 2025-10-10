package com.mss301.kraft.admin_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductRevenueData {
    private String productName;
    private String collectionName;
    private BigDecimal revenue;
    private Long orderCount;
    private Long quantitySold;
    private Double percentage;
}
