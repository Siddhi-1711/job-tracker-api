package com.jobtracker.dto.analytics;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationSummaryResponse {

    private Long id;
    private String companyName;
    private String jobTitle;
    private String status;
    private String source;
    private LocalDate appliedDate;
    private int interviewRoundsCount;
}