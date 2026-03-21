package com.jobtracker.dto.interview;

import com.jobtracker.enums.InterviewType;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class InterviewRoundResponse {

    private Long id;
    private Integer roundNumber;
    private InterviewType interviewType;
    private LocalDateTime scheduledAt;
    private String interviewerName;
    private String meetingLink;
    private String notes;
    private String outcome;
    private Boolean reminderSent;
    private LocalDateTime createdAt;
}