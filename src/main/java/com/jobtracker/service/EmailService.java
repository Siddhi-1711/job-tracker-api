package com.jobtracker.service;

import com.jobtracker.entity.InterviewRound;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    @Async
    public void sendReminderEmail(String recipientEmail, String recipientName,
                                  String companyName, String jobTitle,
                                  InterviewRound round) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(recipientEmail);
            helper.setSubject("Interview Reminder: " + companyName + " — " + jobTitle);
            helper.setText(buildEmailBody(
                    recipientName, companyName, jobTitle, round), true);

            mailSender.send(message);
            log.info("Reminder email sent to {} for {} at {}",
                    recipientEmail, companyName, round.getScheduledAt());

        } catch (MessagingException e) {
            log.error("Failed to send reminder email for round id {}: {}",
                    round.getId(), e.getMessage());
        }
    }

    private String buildEmailBody(String name, String company,
                                  String jobTitle, InterviewRound round) {
        return """
                <html>
                <body style="font-family: Arial, sans-serif; max-width: 600px; margin: auto;">

                    <div style="background-color: #4F46E5; padding: 24px; border-radius: 8px 8px 0 0;">
                        <h1 style="color: white; margin: 0; font-size: 22px;">
                            Interview Reminder
                        </h1>
                    </div>

                    <div style="padding: 24px; border: 1px solid #e5e7eb; border-top: none;
                                border-radius: 0 0 8px 8px;">

                        <p style="font-size: 16px;">Hi <strong>%s</strong>,</p>

                        <p style="font-size: 15px; color: #374151;">
                            You have an upcoming interview scheduled within the next 24 hours.
                        </p>

                        <div style="background-color: #F9FAFB; padding: 16px;
                                    border-radius: 8px; margin: 20px 0;">
                            <table style="width: 100%%; font-size: 14px;">
                                <tr>
                                    <td style="color: #6B7280; padding: 6px 0; width: 140px;">Company</td>
                                    <td style="font-weight: bold; color: #111827;">%s</td>
                                </tr>
                                <tr>
                                    <td style="color: #6B7280; padding: 6px 0;">Role</td>
                                    <td style="font-weight: bold; color: #111827;">%s</td>
                                </tr>
                                <tr>
                                    <td style="color: #6B7280; padding: 6px 0;">Round</td>
                                    <td style="font-weight: bold; color: #111827;">
                                        Round %d — %s
                                    </td>
                                </tr>
                                <tr>
                                    <td style="color: #6B7280; padding: 6px 0;">Scheduled At</td>
                                    <td style="font-weight: bold; color: #111827;">%s</td>
                                </tr>
                                %s
                            </table>
                        </div>

                        %s

                        <p style="font-size: 14px; color: #6B7280; margin-top: 32px;">
                            Best of luck! 🚀<br/>
                            <strong>Job Tracker</strong>
                        </p>
                    </div>

                </body>
                </html>
                """.formatted(
                name,
                company,
                jobTitle,
                round.getRoundNumber(),
                round.getInterviewType(),
                round.getScheduledAt().toString().replace("T", " at "),
                round.getMeetingLink() != null
                        ? """
                          <tr>
                              <td style="color: #6B7280; padding: 6px 0;">Meeting Link</td>
                              <td><a href="%s" style="color: #4F46E5;">Join Meeting</a></td>
                          </tr>
                          """.formatted(round.getMeetingLink())
                        : "",
                round.getInterviewerName() != null
                        ? "<p style=\"font-size:14px;\">Your interviewer will be " +
                        "<strong>" + round.getInterviewerName() + "</strong>.</p>"
                        : ""
        );
    }

    @Async
    public void sendFollowUpReminder(String recipientEmail, String recipientName,
                                     String companyName, String jobTitle,
                                     String status, LocalDate appliedDate) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(recipientEmail);
            helper.setSubject("Follow-up Reminder: " + companyName + " — " + jobTitle);
            helper.setText(buildFollowUpEmailBody(
                    recipientName, companyName, jobTitle, status, appliedDate), true);

            mailSender.send(message);
            log.info("Follow-up reminder sent to {} for {}", recipientEmail, companyName);

        } catch (MessagingException e) {
            log.error("Failed to send follow-up reminder for {}: {}",
                    companyName, e.getMessage());
        }
    }

    private String buildFollowUpEmailBody(String name, String company,
                                          String jobTitle, String status,
                                          LocalDate appliedDate) {
        return """
            <html>
            <body style="font-family: Arial, sans-serif; max-width: 600px; margin: auto;">

                <div style="background-color: #0F766E; padding: 24px; border-radius: 8px 8px 0 0;">
                    <h1 style="color: white; margin: 0; font-size: 22px;">
                        Follow-up Reminder
                    </h1>
                </div>

                <div style="padding: 24px; border: 1px solid #e5e7eb; border-top: none;
                            border-radius: 0 0 8px 8px;">

                    <p style="font-size: 16px;">Hi <strong>%s</strong>,</p>

                    <p style="font-size: 15px; color: #374151;">
                        Today is your scheduled follow-up date for the following application.
                        Consider sending a follow-up email or checking the application status.
                    </p>

                    <div style="background-color: #F9FAFB; padding: 16px;
                                border-radius: 8px; margin: 20px 0;">
                        <table style="width: 100%%; font-size: 14px;">
                            <tr>
                                <td style="color: #6B7280; padding: 6px 0; width: 140px;">Company</td>
                                <td style="font-weight: bold; color: #111827;">%s</td>
                            </tr>
                            <tr>
                                <td style="color: #6B7280; padding: 6px 0;">Role</td>
                                <td style="font-weight: bold; color: #111827;">%s</td>
                            </tr>
                            <tr>
                                <td style="color: #6B7280; padding: 6px 0;">Current Status</td>
                                <td style="font-weight: bold; color: #111827;">%s</td>
                            </tr>
                            <tr>
                                <td style="color: #6B7280; padding: 6px 0;">Applied On</td>
                                <td style="font-weight: bold; color: #111827;">%s</td>
                            </tr>
                        </table>
                    </div>

                    <div style="background-color: #ECFDF5; border-left: 4px solid #0F766E;
                                padding: 12px 16px; border-radius: 0 8px 8px 0; margin: 20px 0;">
                        <p style="margin: 0; font-size: 14px; color: #065F46;">
                            <strong>Tip:</strong> A polite follow-up email can significantly
                            increase your chances of getting a response!
                        </p>
                    </div>

                    <p style="font-size: 14px; color: #6B7280; margin-top: 32px;">
                        Good luck! 💪<br/>
                        <strong>Job Tracker</strong>
                    </p>
                </div>

            </body>
            </html>
            """.formatted(name, company, jobTitle, status,
                appliedDate != null ? appliedDate.toString() : "N/A");
    }
}