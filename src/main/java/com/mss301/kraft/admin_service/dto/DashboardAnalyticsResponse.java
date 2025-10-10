package com.mss301.kraft.admin_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardAnalyticsResponse {
    private Long totalUsers;
    private Long totalProducts;
    private BigDecimal totalRevenue;
    private Long totalOrders;
    private Long todayVisitors;
    
    private Double userGrowth;
    private Double revenueGrowth;
    private Double orderGrowth;
    private Double productGrowth;
    
    private List<SalesDataPoint> salesData;
    private List<ProductSalesData> productSalesData;
    private List<VisitorData> visitorData;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SalesDataPoint {
        private String month;
        private BigDecimal sales;
        private Long orders;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProductSalesData {
        private String name;
        private Double value;
        private BigDecimal sales;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class VisitorData {
        private String day;
        private Long visitors;
    }
}
