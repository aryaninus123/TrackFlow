package com.issuetracker.controller;

import com.issuetracker.dto.AnalyticsResponse;
import com.issuetracker.service.AnalyticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/analytics")
@RequiredArgsConstructor
@Tag(name = "Analytics", description = "Analytics and reporting endpoints")
public class AnalyticsController {
    private final AnalyticsService analyticsService;

    @GetMapping
    @Operation(summary = "Get analytics", description = "Retrieves comprehensive analytics dashboard data")
    public ResponseEntity<AnalyticsResponse> getAnalytics() {
        return ResponseEntity.ok(analyticsService.getAnalytics());
    }
}
