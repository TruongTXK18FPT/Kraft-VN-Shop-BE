package com.mss301.kraft.admin_service.controller;

import com.mss301.kraft.admin_service.dto.VisitorAnalyticsResponse;
import com.mss301.kraft.admin_service.service.VisitorAnalyticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/analytics")
@RequiredArgsConstructor
@Tag(name = "Visitor Analytics", description = "Visitor tracking and analytics APIs")
public class VisitorAnalyticsController {

    private final VisitorAnalyticsService visitorAnalyticsService;

    @PostMapping("/track")
    @Operation(summary = "Track a visitor")
    public ResponseEntity<Map<String, String>> trackVisitor(@RequestBody Map<String, Object> request) {
        try {
            // Extract visitor information from request
            String userAgent = (String) request.get("userAgent");
            String ipAddress = (String) request.get("ipAddress");
            String country = (String) request.get("country");
            String deviceType = (String) request.get("deviceType");

            // Track the visitor
            visitorAnalyticsService.trackVisitor(userAgent, ipAddress, country, deviceType);

            return ResponseEntity.ok(Map.of("status", "success", "message", "Visitor tracked successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("status", "error", "message", e.getMessage()));
        }
    }

    @PostMapping("/track-visit")
    @Operation(summary = "Track a visit from HTTP request")
    public ResponseEntity<Map<String, String>> trackVisit(HttpServletRequest request) {
        visitorAnalyticsService.trackVisit(request);
        return ResponseEntity.ok(Map.of("status", "success", "message", "Visit tracked"));
    }

    @GetMapping("/visitors/today")
    @Operation(summary = "Get today's visitor statistics")
    public ResponseEntity<Map<String, Object>> getTodayStats() {
        Long uniqueVisitors = visitorAnalyticsService.getTodayUniqueVisitors();
        Long totalVisits = visitorAnalyticsService.getTodayTotalVisits();

        return ResponseEntity.ok(Map.of(
                "uniqueVisitors", uniqueVisitors,
                "totalVisits", totalVisits,
                "date", LocalDate.now().toString()));
    }

    @GetMapping("/visitors/stats")
    @Operation(summary = "Get visitor statistics for a period")
    public ResponseEntity<VisitorAnalyticsResponse> getVisitorStats(
            @RequestParam(defaultValue = "7") int days) {
        try {
            VisitorAnalyticsResponse response = visitorAnalyticsService.getVisitorStats(days);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/visitors/stats-legacy")
    @Operation(summary = "Get visitor statistics for a period (legacy format)")
    public ResponseEntity<Map<String, Object>> getVisitorStatsLegacy(
            @RequestParam(defaultValue = "7") int days) {
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(days - 1);

        Map<String, Object> stats = visitorAnalyticsService.getVisitorStatsForPeriod(startDate, endDate);
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/visitors/device-types")
    @Operation(summary = "Get device type statistics")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getDeviceTypeStats(
            @RequestParam(defaultValue = "30") int days) {
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(days - 1);

        return ResponseEntity.ok(Map.of(
                "deviceTypes", visitorAnalyticsService.getDeviceTypeStats(startDate, endDate),
                "period", Map.of("startDate", startDate.toString(), "endDate", endDate.toString())));
    }

    @GetMapping("/visitors/countries")
    @Operation(summary = "Get country statistics")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getCountryStats(
            @RequestParam(defaultValue = "30") int days) {
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(days - 1);

        return ResponseEntity.ok(Map.of(
                "countries", visitorAnalyticsService.getCountryStats(startDate, endDate),
                "period", Map.of("startDate", startDate.toString(), "endDate", endDate.toString())));
    }

    @PostMapping("/test/create-sample-data")
    @Operation(summary = "Create sample visitor data for testing")
    public ResponseEntity<Map<String, String>> createSampleData() {
        try {
            visitorAnalyticsService.createSampleData();
            return ResponseEntity.ok(Map.of("message", "Sample data created successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/test/simple-stats")
    @Operation(summary = "Get simple visitor stats for testing")
    public ResponseEntity<Map<String, Object>> getSimpleStats() {
        try {
            Long todayVisitors = visitorAnalyticsService.getTodayUniqueVisitors();
            Long todayVisits = visitorAnalyticsService.getTodayTotalVisits();

            Map<String, Object> result = Map.of(
                    "todayVisitors", todayVisitors,
                    "todayVisits", todayVisits,
                    "message", "Simple stats retrieved successfully");

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/test/device-stats")
    @Operation(summary = "Test device stats query")
    public ResponseEntity<Map<String, Object>> testDeviceStats() {
        try {
            LocalDate endDate = LocalDate.now();
            LocalDate startDate = endDate.minusDays(6);

            List<Map<String, Object>> deviceStats = visitorAnalyticsService.getDeviceTypeStats(startDate, endDate);

            Map<String, Object> result = Map.of(
                    "deviceStats", deviceStats,
                    "period", Map.of("startDate", startDate.toString(), "endDate", endDate.toString()),
                    "message", "Device stats retrieved successfully");

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/test/raw-query")
    @Operation(summary = "Test raw repository query")
    public ResponseEntity<Map<String, Object>> testRawQuery() {
        try {
            LocalDate endDate = LocalDate.now();
            LocalDate startDate = endDate.minusDays(6);

            // Test simple count query first
            Long totalVisits = visitorAnalyticsService.getTodayTotalVisits();
            Long uniqueVisitors = visitorAnalyticsService.getTodayUniqueVisitors();

            Map<String, Object> result = Map.of(
                    "totalVisits", totalVisits,
                    "uniqueVisitors", uniqueVisitors,
                    "period", Map.of("startDate", startDate.toString(), "endDate", endDate.toString()),
                    "message", "Raw query test successful");

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/test/visitor-stats-simple")
    @Operation(summary = "Test visitor stats with simple response")
    public ResponseEntity<Map<String, Object>> testVisitorStatsSimple() {
        try {
            LocalDate endDate = LocalDate.now();
            LocalDate startDate = endDate.minusDays(6);

            // Get basic stats using existing method
            Map<String, Object> stats = visitorAnalyticsService.getVisitorStatsForPeriod(startDate, endDate);
            Long totalVisits = (Long) stats.get("totalVisits");
            Long uniqueVisitors = (Long) stats.get("uniqueVisitors");

            Map<String, Object> result = Map.of(
                    "totalVisits", totalVisits,
                    "uniqueVisitors", uniqueVisitors,
                    "period", Map.of("startDate", startDate.toString(), "endDate", endDate.toString()),
                    "message", "Simple visitor stats retrieved successfully");

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}