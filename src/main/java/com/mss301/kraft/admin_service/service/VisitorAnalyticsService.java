package com.mss301.kraft.admin_service.service;

import com.mss301.kraft.admin_service.dto.VisitorAnalyticsResponse;
import com.mss301.kraft.admin_service.entity.VisitorAnalytics;
import com.mss301.kraft.admin_service.repository.VisitorAnalyticsRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class VisitorAnalyticsService {

    private final VisitorAnalyticsRepository visitorAnalyticsRepository;

    @Transactional
    public void trackVisitor(String userAgent, String ipAddress, String country, String deviceType) {
        try {
            LocalDate today = LocalDate.now();

            // Check if this IP already visited today
            boolean isUniqueToday = !visitorAnalyticsRepository.existsByIpAddressAndVisitDate(ipAddress, today);

            VisitorAnalytics analytics = VisitorAnalytics.builder()
                    .visitDate(today)
                    .ipAddress(ipAddress)
                    .userAgent(userAgent)
                    .isUniqueVisitor(isUniqueToday)
                    .deviceType(deviceType != null ? deviceType : detectDeviceType(userAgent))
                    .country(country != null ? country : "Unknown")
                    .build();

            visitorAnalyticsRepository.save(analytics);

            log.debug("Tracked visitor from IP: {} on {}", ipAddress, today);
        } catch (Exception e) {
            log.error("Error tracking visitor", e);
        }
    }

    @Transactional
    public void trackVisit(HttpServletRequest request) {
        try {
            String ipAddress = getClientIpAddress(request);
            LocalDate today = LocalDate.now();

            // Check if this IP already visited today
            boolean isUniqueToday = !visitorAnalyticsRepository.existsByIpAddressAndVisitDate(ipAddress, today);

            VisitorAnalytics analytics = VisitorAnalytics.builder()
                    .visitDate(today)
                    .ipAddress(ipAddress)
                    .userAgent(request.getHeader("User-Agent"))
                    .referrer(request.getHeader("Referer"))
                    .pageUrl(request.getRequestURL().toString())
                    .sessionId(request.getSession().getId())
                    .isUniqueVisitor(isUniqueToday)
                    .deviceType(detectDeviceType(request.getHeader("User-Agent")))
                    .browser(detectBrowser(request.getHeader("User-Agent")))
                    .os(detectOS(request.getHeader("User-Agent")))
                    .build();

            visitorAnalyticsRepository.save(analytics);

            log.debug("Tracked visit from IP: {} on {}", ipAddress, today);
        } catch (Exception e) {
            log.error("Error tracking visit", e);
        }
    }

    public Long getTodayUniqueVisitors() {
        return visitorAnalyticsRepository.countUniqueVisitorsByDate(LocalDate.now());
    }

    public Long getTodayTotalVisits() {
        return visitorAnalyticsRepository.countTotalVisitsByDate(LocalDate.now());
    }

    public Long getTotalRecords() {
        return visitorAnalyticsRepository.count();
    }

    public VisitorAnalyticsResponse getVisitorStats(int days) {
        try {
            LocalDate endDate = LocalDate.now();
            LocalDate startDate = endDate.minusDays(days - 1);

            // Get today's stats with null safety
            Long todayUnique = getTodayUniqueVisitors();
            Long todayTotal = getTodayTotalVisits();
            
            VisitorAnalyticsResponse.TodayStats todayStats = VisitorAnalyticsResponse.TodayStats.builder()
                    .uniqueVisitors(todayUnique != null ? todayUnique.intValue() : 0)
                    .totalVisits(todayTotal != null ? todayTotal.intValue() : 0)
                    .date(endDate.toString())
                    .build();

            // Get device type stats with null safety
            List<VisitorAnalyticsResponse.DeviceTypeStats> deviceTypes = getDeviceTypeStats(startDate, endDate)
                    .stream()
                    .map(stat -> {
                        String deviceType = (String) stat.get("deviceType");
                        Object visitorsObj = stat.get("visitors");
                        int visitors = 0;
                        if (visitorsObj instanceof Number) {
                            visitors = ((Number) visitorsObj).intValue();
                        }
                        return VisitorAnalyticsResponse.DeviceTypeStats.builder()
                                .deviceType(deviceType != null ? deviceType : "Unknown")
                                .visitors(visitors)
                                .build();
                    })
                    .collect(Collectors.toList());

            // Get country stats with null safety
            List<VisitorAnalyticsResponse.CountryStats> countries = getCountryStats(startDate, endDate)
                    .stream()
                    .map(stat -> {
                        String country = (String) stat.get("country");
                        Object visitorsObj = stat.get("visitors");
                        int visitors = 0;
                        if (visitorsObj instanceof Number) {
                            visitors = ((Number) visitorsObj).intValue();
                        }
                        return VisitorAnalyticsResponse.CountryStats.builder()
                                .country(country != null ? country : "Unknown")
                                .visitors(visitors)
                                .build();
                    })
                    .collect(Collectors.toList());

            // Get daily stats with null safety
            List<VisitorAnalyticsResponse.DailyStats> dailyStats = visitorAnalyticsRepository
                    .getVisitorStatsBetween(startDate, endDate)
                    .stream()
                    .map(stat -> {
                        String date = stat[0] != null ? stat[0].toString() : endDate.toString();
                        int uniqueVisitors = 0;
                        int totalVisits = 0;
                        if (stat[1] instanceof Number) {
                            uniqueVisitors = ((Number) stat[1]).intValue();
                        }
                        if (stat[2] instanceof Number) {
                            totalVisits = ((Number) stat[2]).intValue();
                        }
                        return VisitorAnalyticsResponse.DailyStats.builder()
                                .date(date)
                                .uniqueVisitors(uniqueVisitors)
                                .totalVisits(totalVisits)
                                .build();
                    })
                    .collect(Collectors.toList());

            return VisitorAnalyticsResponse.builder()
                    .todayStats(todayStats)
                    .deviceTypes(deviceTypes)
                    .countries(countries)
                    .dailyStats(dailyStats)
                    .build();
        } catch (Exception e) {
            log.error("Error getting visitor stats", e);
            // Return empty response instead of throwing
            return VisitorAnalyticsResponse.builder()
                    .todayStats(VisitorAnalyticsResponse.TodayStats.builder()
                            .uniqueVisitors(0)
                            .totalVisits(0)
                            .date(LocalDate.now().toString())
                            .build())
                    .deviceTypes(List.of())
                    .countries(List.of())
                    .dailyStats(List.of())
                    .build();
        }
    }

    public Map<String, Object> getVisitorStatsForPeriod(LocalDate startDate, LocalDate endDate) {
        List<Object[]> stats = visitorAnalyticsRepository.getVisitorStatsBetween(startDate, endDate);

        return Map.of(
                "uniqueVisitors", visitorAnalyticsRepository.countUniqueVisitorsBetween(startDate, endDate),
                "totalVisits", visitorAnalyticsRepository.countTotalVisitsBetween(startDate, endDate),
                "dailyStats", stats.stream().map(stat -> Map.of(
                        "date", stat[0],
                        "uniqueVisitors", stat[1],
                        "totalVisits", stat[2])).collect(Collectors.toList()));
    }

    public List<Map<String, Object>> getDeviceTypeStats(LocalDate startDate, LocalDate endDate) {
        try {
            List<Object[]> results = visitorAnalyticsRepository.getDeviceTypeStats(startDate, endDate);

            return results.stream()
                    .map(stat -> Map.of(
                            "deviceType", stat[0] != null ? stat[0] : "Unknown",
                            "visitors", stat[1]))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw e;
        }
    }

    public List<Map<String, Object>> getCountryStats(LocalDate startDate, LocalDate endDate) {
        return visitorAnalyticsRepository.getCountryStats(startDate, endDate)
                .stream()
                .map(stat -> Map.of(
                        "country", stat[0],
                        "visitors", stat[1]))
                .collect(Collectors.toList());
    }

    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty() && !"unknown".equalsIgnoreCase(xForwardedFor)) {
            return xForwardedFor.split(",")[0].trim();
        }

        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty() && !"unknown".equalsIgnoreCase(xRealIp)) {
            return xRealIp;
        }

        return request.getRemoteAddr();
    }

    private String detectDeviceType(String userAgent) {
        if (userAgent == null)
            return "Unknown";

        String ua = userAgent.toLowerCase();
        if (ua.contains("mobile") || ua.contains("android") || ua.contains("iphone")) {
            return "Mobile";
        } else if (ua.contains("tablet") || ua.contains("ipad")) {
            return "Tablet";
        } else {
            return "Desktop";
        }
    }

    private String detectBrowser(String userAgent) {
        if (userAgent == null)
            return "Unknown";

        String ua = userAgent.toLowerCase();
        if (ua.contains("chrome"))
            return "Chrome";
        if (ua.contains("firefox"))
            return "Firefox";
        if (ua.contains("safari"))
            return "Safari";
        if (ua.contains("edge"))
            return "Edge";
        if (ua.contains("opera"))
            return "Opera";
        return "Other";
    }

    private String detectOS(String userAgent) {
        if (userAgent == null)
            return "Unknown";

        String ua = userAgent.toLowerCase();
        if (ua.contains("windows"))
            return "Windows";
        if (ua.contains("mac"))
            return "macOS";
        if (ua.contains("linux"))
            return "Linux";
        if (ua.contains("android"))
            return "Android";
        if (ua.contains("ios"))
            return "iOS";
        return "Other";
    }

    @Transactional
    public void createSampleData() {
        // Creating sample visitor data

        // Create sample data for the last 7 days
        for (int i = 0; i < 7; i++) {
            LocalDate date = LocalDate.now().minusDays(i);

            // Create some sample visitors for each day
            for (int j = 0; j < 5 + (int) (Math.random() * 10); j++) {
                VisitorAnalytics visitor = VisitorAnalytics.builder()
                        .ipAddress("192.168.1." + (100 + j))
                        .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                        .country("Vietnam")
                        .deviceType("Desktop")
                        .browser("Chrome")
                        .os("Windows")
                        .visitDate(date)
                        .build();

                visitorAnalyticsRepository.save(visitor);
            }
        }

        // Sample data created successfully
    }
}
