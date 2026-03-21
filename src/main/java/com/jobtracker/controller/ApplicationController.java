package com.jobtracker.controller;

import com.jobtracker.dto.ApiResponse;
import com.jobtracker.dto.application.*;
import com.jobtracker.enums.ApplicationStatus;
import com.jobtracker.service.ApplicationService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;
import java.util.List;

@RestController
@RequestMapping("/api/v1/applications")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("isAuthenticated()")
public class ApplicationController {

    private final ApplicationService applicationService;

    @PostMapping
    public ResponseEntity<ApiResponse<ApplicationResponse>> create(
            @Valid @RequestBody ApplicationRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Application created",
                        applicationService.create(request, userDetails.getUsername())));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ApplicationResponse>>> getAll(
            @RequestParam(required = false) ApplicationStatus status,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(ApiResponse.success(
                applicationService.getAll(userDetails.getUsername(), status)));
    }

    @GetMapping("/paged")
    public ResponseEntity<ApiResponse<PagedResponse<ApplicationResponse>>> getAllPaged(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam(required = false) ApplicationStatus status,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(ApiResponse.success(
                applicationService.getAllPaged(
                        userDetails.getUsername(), status, page, size, sortBy, sortDir)));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<ApplicationResponse>>> search(
            @ModelAttribute ApplicationSearchRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(ApiResponse.success(
                applicationService.search(request, userDetails.getUsername())));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ApplicationResponse>> getById(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(ApiResponse.success(
                applicationService.getById(id, userDetails.getUsername())));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ApplicationResponse>> update(
            @PathVariable Long id,
            @Valid @RequestBody ApplicationRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(ApiResponse.success("Application updated",
                applicationService.update(id, request, userDetails.getUsername())));
    }



    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        applicationService.delete(id, userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success("Application deleted", null));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<ApplicationResponse>> updateStatus(
            @PathVariable Long id,
            @RequestParam ApplicationStatus status,
            @RequestParam(required = false) String note,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(ApiResponse.success("Status updated",
                applicationService.updateStatus(
                        id, status, userDetails.getUsername(), note)));
    }

    @GetMapping("/{id}/history")
    public ResponseEntity<ApiResponse<List<StatusHistoryResponse>>> getStatusHistory(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(ApiResponse.success(
                applicationService.getStatusHistory(id, userDetails.getUsername())));
    }
}