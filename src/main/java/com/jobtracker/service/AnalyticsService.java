package com.jobtracker.service;

import com.jobtracker.dto.analytics.ApplicationSummaryResponse;
import com.jobtracker.dto.analytics.FunnelStatsResponse;
import com.jobtracker.entity.JobApplication;
import com.jobtracker.entity.User;
import com.jobtracker.enums.ApplicationStatus;
import com.jobtracker.exception.ResourceNotFoundException;
import com.jobtracker.repository.ApplicationRepository;
import com.jobtracker.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AnalyticsService {

    private final ApplicationRepository applicationRepository;
    private final UserRepository userRepository;

    private User getUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    @Transactional(readOnly = true)
    public FunnelStatsResponse getFunnelStats(String email) {
        User user = getUser(email);

        long total      = applicationRepository.countByUserAndDeletedFalse(user);
        long applied    = applicationRepository.countByUserAndStatusAndDeletedFalse(user, ApplicationStatus.APPLIED);
        long screening  = applicationRepository.countByUserAndStatusAndDeletedFalse(user, ApplicationStatus.SCREENING);
        long interview  = applicationRepository.countByUserAndStatusAndDeletedFalse(user, ApplicationStatus.INTERVIEW);
        long offer      = applicationRepository.countByUserAndStatusAndDeletedFalse(user, ApplicationStatus.OFFER);
        long rejected   = applicationRepository.countByUserAndStatusAndDeletedFalse(user, ApplicationStatus.REJECTED);
        long withdrawn  = applicationRepository.countByUserAndStatusAndDeletedFalse(user, ApplicationStatus.WITHDRAWN);

        double offerRate     = total > 0 ? Math.round((offer * 100.0 / total) * 100.0) / 100.0 : 0;
        double rejectionRate = total > 0 ? Math.round((rejected * 100.0 / total) * 100.0) / 100.0 : 0;

        return FunnelStatsResponse.builder()
                .totalApplications(total)
                .applied(applied)
                .screening(screening)
                .interview(interview)
                .offer(offer)
                .rejected(rejected)
                .withdrawn(withdrawn)
                .offerRate(offerRate)
                .rejectionRate(rejectionRate)
                .build();
    }

    @Transactional(readOnly = true)
    public List<ApplicationSummaryResponse> getApplicationSummary(String email) {
        User user = getUser(email);

        return applicationRepository.findByUserAndDeletedFalseOrderByCreatedAtDesc(user)
                .stream()
                .map(this::toSummary)
                .toList();
    }

    private ApplicationSummaryResponse toSummary(JobApplication app) {
        return ApplicationSummaryResponse.builder()
                .id(app.getId())
                .companyName(app.getCompanyName())
                .jobTitle(app.getJobTitle())
                .status(app.getStatus().name())
                .source(app.getSource() != null ? app.getSource().name() : "N/A")
                .appliedDate(app.getAppliedDate())
                .interviewRoundsCount(app.getInterviewRounds().size())
                .build();
    }
}