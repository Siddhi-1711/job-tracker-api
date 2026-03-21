package com.jobtracker.controller;

import com.jobtracker.dto.ApiResponse;
import com.jobtracker.dto.analytics.ApplicationSummaryResponse;
import com.jobtracker.dto.analytics.FunnelStatsResponse;
import com.jobtracker.service.AnalyticsService;
import com.jobtracker.service.CsvExportService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/analytics")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("isAuthenticated()")
public class AnalyticsController {

    private final AnalyticsService analyticsService;
    private final CsvExportService csvExportService;

    @GetMapping("/funnel")
    public ResponseEntity<ApiResponse<FunnelStatsResponse>> getFunnelStats(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(ApiResponse.success(
                analyticsService.getFunnelStats(userDetails.getUsername())));
    }

    @GetMapping("/summary")
    public ResponseEntity<ApiResponse<List<ApplicationSummaryResponse>>> getSummary(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(ApiResponse.success(
                analyticsService.getApplicationSummary(userDetails.getUsername())));
    }

    @GetMapping("/export/csv")
    public ResponseEntity<byte[]> exportCsv(
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            String csvContent = csvExportService.exportApplicationsToCsv(
                    userDetails.getUsername());
            byte[] csvBytes = csvContent.getBytes();
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=job-applications.csv")
                    .contentType(MediaType.parseMediaType("text/csv"))
                    .contentLength(csvBytes.length)
                    .body(csvBytes);
        } catch (IOException e) {
            throw new RuntimeException("Failed to export CSV: " + e.getMessage());
        }
    }
}