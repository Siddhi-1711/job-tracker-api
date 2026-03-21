package com.jobtracker.dto.interview;

import com.jobtracker.enums.InterviewType;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class InterviewRoundRequest {

    @NotNull(message = "Round number is required")
    private Integer roundNumber;

    @NotNull(message = "Interview type is required")
    private InterviewType interviewType;

    @NotNull(message = "Scheduled date/time is required")
    private LocalDateTime scheduledAt;

    private String interviewerName;
    private String meetingLink;
    private String notes;
    private String outcome;
}