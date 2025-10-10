package com.mss301.kraft.admin_service.service;

import com.mss301.kraft.admin_service.dto.DashboardAnalyticsResponse;
import com.mss301.kraft.order_service.repository.OrderRepository;
import com.mss301.kraft.product_service.repository.ProductRepository;
import com.mss301.kraft.user_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;

    public DashboardAnalyticsResponse getAnalytics() {
        // Get current totals
        Long totalUsers = userRepository.count();
        Long totalProducts = productRepository.count();
        Long totalOrders = orderRepository.count();
        
        // Calculate total revenue from all completed orders
        BigDecimal totalRevenue = orderRepository.sumTotalRevenue();
        if (totalRevenue == null) {
            totalRevenue = BigDecimal.ZERO;
        }
        
        // Calculate growth rates (comparing with previous period)
        OffsetDateTime monthAgo = OffsetDateTime.now().minusMonths(1);
        Long usersMonthAgo = userRepository.countByCreatedAtBefore(monthAgo);
        Long productsMonthAgo = productRepository.countByCreatedAtBefore(monthAgo);
        Long ordersMonthAgo = orderRepository.countByCreatedAtBefore(monthAgo);
        BigDecimal revenueMonthAgo = orderRepository.sumRevenueByCreatedAtBefore(monthAgo);
        if (revenueMonthAgo == null) {
            revenueMonthAgo = BigDecimal.ZERO;
        }
        
        Double userGrowth = calculateGrowthRate(usersMonthAgo, totalUsers);
        Double productGrowth = calculateGrowthRate(productsMonthAgo, totalProducts);
        Double orderGrowth = calculateGrowthRate(ordersMonthAgo, totalOrders);
        Double revenueGrowth = calculateGrowthRate(revenueMonthAgo.doubleValue(), totalRevenue.doubleValue());
        
        // Today's visitors (mock data - would need analytics service)
        Long todayVisitors = 1240L;
        
        // Generate sales data for last 12 months
        List<DashboardAnalyticsResponse.SalesDataPoint> salesData = generateSalesData();
        
        // Generate product sales distribution
        List<DashboardAnalyticsResponse.ProductSalesData> productSalesData = generateProductSalesData(totalRevenue);
        
        // Generate visitor data for last 7 days
        List<DashboardAnalyticsResponse.VisitorData> visitorData = generateVisitorData();
        
        return DashboardAnalyticsResponse.builder()
                .totalUsers(totalUsers)
                .totalProducts(totalProducts)
                .totalRevenue(totalRevenue)
                .totalOrders(totalOrders)
                .todayVisitors(todayVisitors)
                .userGrowth(userGrowth)
                .revenueGrowth(revenueGrowth)
                .orderGrowth(orderGrowth)
                .productGrowth(productGrowth)
                .salesData(salesData)
                .productSalesData(productSalesData)
                .visitorData(visitorData)
                .build();
    }
    
    private Double calculateGrowthRate(Long oldValue, Long newValue) {
        if (oldValue == null || oldValue == 0) return 0.0;
        return ((newValue - oldValue) * 100.0) / oldValue;
    }
    
    private Double calculateGrowthRate(Double oldValue, Double newValue) {
        if (oldValue == null || oldValue == 0) return 0.0;
        return ((newValue - oldValue) * 100.0) / oldValue;
    }
    
    private List<DashboardAnalyticsResponse.SalesDataPoint> generateSalesData() {
        List<DashboardAnalyticsResponse.SalesDataPoint> data = new ArrayList<>();
        LocalDate now = LocalDate.now();
        
        for (int i = 11; i >= 0; i--) {
            LocalDate month = now.minusMonths(i);
            OffsetDateTime startOfMonth = month.withDayOfMonth(1).atStartOfDay().atOffset(OffsetDateTime.now().getOffset());
            OffsetDateTime endOfMonth = month.withDayOfMonth(month.lengthOfMonth()).atTime(23, 59, 59).atOffset(OffsetDateTime.now().getOffset());
            
            // Get actual data from database
            BigDecimal monthlySales = orderRepository.sumRevenueBetween(startOfMonth, endOfMonth);
            Long monthlyOrders = orderRepository.countByCreatedAtBetween(startOfMonth, endOfMonth);
            
            if (monthlySales == null) monthlySales = BigDecimal.ZERO;
            if (monthlyOrders == null) monthlyOrders = 0L;
            
            String monthName = "T" + month.getMonthValue();
            
            data.add(DashboardAnalyticsResponse.SalesDataPoint.builder()
                    .month(monthName)
                    .sales(monthlySales)
                    .orders(monthlyOrders)
                    .build());
        }
        
        return data;
    }
    
    private List<DashboardAnalyticsResponse.ProductSalesData> generateProductSalesData(BigDecimal totalRevenue) {
        // This would ideally come from actual product sales data
        // For now, using mock distribution
        List<DashboardAnalyticsResponse.ProductSalesData> data = new ArrayList<>();
        
        data.add(DashboardAnalyticsResponse.ProductSalesData.builder()
                .name("Pegasus Series")
                .value(45)
                .sales(totalRevenue.multiply(new BigDecimal("0.45")).setScale(0, RoundingMode.HALF_UP))
                .build());
                
        data.add(DashboardAnalyticsResponse.ProductSalesData.builder()
                .name("Eros Collection")
                .value(30)
                .sales(totalRevenue.multiply(new BigDecimal("0.30")).setScale(0, RoundingMode.HALF_UP))
                .build());
                
        data.add(DashboardAnalyticsResponse.ProductSalesData.builder()
                .name("Alpha Series")
                .value(25)
                .sales(totalRevenue.multiply(new BigDecimal("0.25")).setScale(0, RoundingMode.HALF_UP))
                .build());
        
        return data;
    }
    
    private List<DashboardAnalyticsResponse.VisitorData> generateVisitorData() {
        List<DashboardAnalyticsResponse.VisitorData> data = new ArrayList<>();
        LocalDate now = LocalDate.now();
        Random random = new Random(now.toEpochDay()); // Seed for consistency
        
        for (int i = 6; i >= 0; i--) {
            LocalDate day = now.minusDays(i);
            String dayName = getDayName(day);
            Long visitors = 800L + random.nextInt(850);
            
            data.add(DashboardAnalyticsResponse.VisitorData.builder()
                    .day(dayName)
                    .visitors(visitors)
                    .build());
        }
        
        return data;
    }
    
    private String getDayName(LocalDate date) {
        int dayOfWeek = date.getDayOfWeek().getValue();
        switch (dayOfWeek) {
            case 1: return "T2";
            case 2: return "T3";
            case 3: return "T4";
            case 4: return "T5";
            case 5: return "T6";
            case 6: return "T7";
            case 7: return "CN";
            default: return "";
        }
    }
}
