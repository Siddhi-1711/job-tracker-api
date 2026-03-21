package com.jobtracker.scheduler;

import com.jobtracker.entity.InterviewRound;
import com.jobtracker.entity.JobApplication;
import com.jobtracker.repository.InterviewRoundRepository;
import com.jobtracker.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import com.jobtracker.repository.ApplicationRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class InterviewReminderScheduler {

    private final InterviewRoundRepository interviewRoundRepository;
    private final EmailService emailService;
    private final ApplicationRepository applicationRepository;

    @Scheduled(cron = "0 0 8 * * *")
    @Transactional
    public void sendInterviewReminders() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime next24Hours = now.plusHours(24);

        log.info("Running interview reminder scheduler at {}", now);

        List<InterviewRound> upcomingInterviews =
                interviewRoundRepository.findUpcomingInterviewsForReminder(now, next24Hours);

        if (upcomingInterviews.isEmpty()) {
            log.info("No upcoming interviews found for reminders");
            return;
        }

        log.info("Found {} upcoming interviews — sending reminders", upcomingInterviews.size());

        for (InterviewRound round : upcomingInterviews) {
            // Extract all data inside the transaction BEFORE async call
            String recipientEmail = round.getJobApplication().getUser().getEmail();
            String recipientName  = round.getJobApplication().getUser().getName();
            String companyName    = round.getJobApplication().getCompanyName();
            String jobTitle       = round.getJobApplication().getJobTitle();

            round.setReminderSent(true);
            interviewRoundRepository.save(round);

            // Now pass plain strings to async method — no lazy loading needed
            emailService.sendReminderEmail(recipientEmail, recipientName,
                    companyName, jobTitle, round);

            log.info("Reminder queued for interview round id {}", round.getId());
        }
    }
    @Scheduled(cron = "0 0 9 * * *")
    @Transactional
    public void sendFollowUpReminders() {
        LocalDate today = LocalDate.now();

        log.info("Running follow-up reminder scheduler for date {}", today);

        List<JobApplication> applications =
                applicationRepository.findApplicationsDueForFollowUp(today);

        if (applications.isEmpty()) {
            log.info("No follow-up reminders for today");
            return;
        }

        log.info("Found {} applications due for follow-up", applications.size());

        for (JobApplication app : applications) {
            String recipientEmail = app.getUser().getEmail();
            String recipientName  = app.getUser().getName();
            String companyName    = app.getCompanyName();
            String jobTitle       = app.getJobTitle();
            String status         = app.getStatus().name();
            LocalDate appliedDate = app.getAppliedDate();

            emailService.sendFollowUpReminder(
                    recipientEmail, recipientName,
                    companyName, jobTitle,
                    status, appliedDate);

            log.info("Follow-up reminder queued for {} at {}",
                    companyName, recipientEmail);
        }
    }
}