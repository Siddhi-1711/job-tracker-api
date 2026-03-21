package com.jobtracker.controller;

import com.jobtracker.dto.ApiResponse;
import com.jobtracker.dto.interview.InterviewRoundRequest;
import com.jobtracker.dto.interview.InterviewRoundResponse;
import com.jobtracker.service.InterviewService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/applications/{applicationId}/interviews")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("isAuthenticated()")
public class InterviewController {

    private final InterviewService interviewService;

    @PostMapping
    public ResponseEntity<ApiResponse<InterviewRoundResponse>> addRound(
            @PathVariable Long applicationId,
            @Valid @RequestBody InterviewRoundRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Interview round added",
                        interviewService.addRound(
                                applicationId, request, userDetails.getUsername())));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<InterviewRoundResponse>>> getRounds(
            @PathVariable Long applicationId,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(ApiResponse.success(
                interviewService.getRounds(applicationId, userDetails.getUsername())));
    }

    @PutMapping("/{roundId}")
    public ResponseEntity<ApiResponse<InterviewRoundResponse>> updateRound(
            @PathVariable Long applicationId,
            @PathVariable Long roundId,
            @Valid @RequestBody InterviewRoundRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(ApiResponse.success("Interview round updated",
                interviewService.updateRound(
                        applicationId, roundId, request, userDetails.getUsername())));
    }

    @DeleteMapping("/{roundId}")
    public ResponseEntity<ApiResponse<Void>> deleteRound(
            @PathVariable Long applicationId,
            @PathVariable Long roundId,
            @AuthenticationPrincipal UserDetails userDetails) {
        interviewService.deleteRound(applicationId, roundId, userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success("Interview round deleted", null));
    }
}