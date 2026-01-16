package com.example.backgestcom.dashboard.api;

import com.example.backgestcom.dashboard.api.dtos.DashboardMetricsResponse;
import com.example.backgestcom.dashboard.application.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/metrics")
    public DashboardMetricsResponse getMetrics() {
        return dashboardService.getMetrics();
    }
}