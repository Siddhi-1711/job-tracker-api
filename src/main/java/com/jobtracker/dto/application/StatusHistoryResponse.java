package com.jobtracker.dto.application;

import com.jobtracker.enums.ApplicationStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class StatusHistoryResponse {

    private Long id;
    private ApplicationStatus oldStatus;
    private ApplicationStatus newStatus;
    private LocalDateTime changedAt;
    private String note;
}