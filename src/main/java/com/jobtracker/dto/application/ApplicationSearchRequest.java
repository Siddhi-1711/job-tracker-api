package com.jobtracker.dto.application;

import com.jobtracker.enums.ApplicationSource;
import com.jobtracker.enums.ApplicationStatus;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
public class ApplicationSearchRequest {

    private String keyword;
    private ApplicationStatus status;
    private ApplicationSource source;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate startDate;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate endDate;
}