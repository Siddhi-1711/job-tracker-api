package com.jobtracker.controller;

import com.jobtracker.dto.ApiResponse;
import com.jobtracker.scheduler.InterviewReminderScheduler;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/dev")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("isAuthenticated()")
@Profile("dev")
@Slf4j
@Tag(name = "Dev Tools", description = "Development-only endpoints — not available in production")
public class SchedulerController {

    private final InterviewReminderScheduler scheduler;

    @PostMapping("/trigger-reminders")
    public ResponseEntity<ApiResponse<Void>> triggerReminders() {
        log.warn("Manual reminder trigger invoked via dev endpoint");
        scheduler.sendInterviewReminders();
        return ResponseEntity.ok(
                ApiResponse.success("Reminder job triggered successfully", null));
    }

    @PostMapping("/trigger-followups")
    public ResponseEntity<ApiResponse<Void>> triggerFollowUps() {
        log.warn("Manual follow-up trigger invoked via dev endpoint");
        scheduler.sendFollowUpReminders();
        return ResponseEntity.ok(
                ApiResponse.success("Follow-up reminder job triggered successfully", null));
    }
}