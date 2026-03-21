package com.jobtracker.controller;

import com.jobtracker.dto.ApiResponse;
import com.jobtracker.scheduler.InterviewReminderScheduler;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
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
public class SchedulerController {

    private final InterviewReminderScheduler scheduler;

    @PostMapping("/trigger-reminders")
    public ResponseEntity<ApiResponse<Void>> triggerReminders() {
        scheduler.sendInterviewReminders();
        return ResponseEntity.ok(
                ApiResponse.success("Reminder job triggered successfully", null));
    }

    @PostMapping("/trigger-followups")
    public ResponseEntity<ApiResponse<Void>> triggerFollowUps() {
        scheduler.sendFollowUpReminders();
        return ResponseEntity.ok(
                ApiResponse.success("Follow-up reminder job triggered successfully", null));
    }
}