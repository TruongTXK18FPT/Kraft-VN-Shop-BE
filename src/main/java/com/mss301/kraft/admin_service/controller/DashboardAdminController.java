package com.mss301.kraft.admin_service.controller;

import com.mss301.kraft.admin_service.dto.DashboardAnalyticsResponse;
import com.mss301.kraft.admin_service.service.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/dashboard")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Dashboard Admin", description = "Dashboard analytics APIs")
public class DashboardAdminController {

    private final DashboardService dashboardService;

    @GetMapping("/analytics")
    @Operation(summary = "Get dashboard analytics data")
    public ResponseEntity<DashboardAnalyticsResponse> getAnalytics() {
        return ResponseEntity.ok(dashboardService.getAnalytics());
    }
}
