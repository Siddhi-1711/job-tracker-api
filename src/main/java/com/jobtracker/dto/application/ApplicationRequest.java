package com.jobtracker.dto.application;

import com.jobtracker.enums.ApplicationSource;
import com.jobtracker.enums.ApplicationStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

@Data
public class ApplicationRequest {

    @NotBlank(message = "Company name is required")
    @Size(max = 100, message = "Company name cannot exceed 100 characters")
    private String companyName;

    @NotBlank(message = "Job title is required")
    @Size(max = 100, message = "Job title cannot exceed 100 characters")
    private String jobTitle;

    @Size(max = 255, message = "Job URL cannot exceed 255 characters")
    private String jobUrl;

    @Size(max = 100, message = "Location cannot exceed 100 characters")
    private String location;

    private ApplicationStatus status;
    private ApplicationSource source;
    private LocalDate appliedDate;
    private LocalDate followUpDate;

    @Size(max = 1000, message = "Notes cannot exceed 1000 characters")
    private String notes;

    @Min(value = 0, message = "Salary expectation cannot be negative")
    private Integer salaryExpectation;

    @Size(max = 255, message = "Referral person name too long")
    private String referralPerson;

    @Size(max = 255, message = "Recruiter name too long")
    private String recruiterName;

    @Email(message = "Invalid recruiter email")
    private String recruiterEmail;

    @Size(max = 20, message = "Phone number too long")
    private String recruiterPhone;

    @Size(max = 100, message = "Resume version too long")
    private String resumeVersion;


}