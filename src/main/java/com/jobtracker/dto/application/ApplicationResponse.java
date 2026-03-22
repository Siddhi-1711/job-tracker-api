package com.jobtracker.dto.application;

import com.jobtracker.dto.interview.InterviewRoundResponse;
import com.jobtracker.enums.ApplicationSource;
import com.jobtracker.enums.ApplicationStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class ApplicationResponse {

    private Long id;
    private String companyName;
    private String jobTitle;
    private String jobUrl;
    private String location;
    private ApplicationStatus status;
    private ApplicationSource source;
    private LocalDate appliedDate;
    private LocalDate followUpDate;
    private String notes;
    private Integer salaryExpectation;
    private String referralPerson;
    private String recruiterName;
    private String recruiterEmail;
    private String recruiterPhone;
    private String resumeVersion;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<InterviewRoundResponse> interviewRounds;
}