package com.mss301.kraft.admin_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VisitorAnalyticsResponse {
    
    private TodayStats todayStats;
    private List<DeviceTypeStats> deviceTypes;
    private List<CountryStats> countries;
    private List<DailyStats> dailyStats;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TodayStats {
        private int uniqueVisitors;
        private int totalVisits;
        private String date;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DeviceTypeStats {
        private String deviceType;
        private int visitors;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CountryStats {
        private String country;
        private int visitors;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DailyStats {
        private String date;
        private int uniqueVisitors;
        private int totalVisits;
    }
}
