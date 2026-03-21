package com.jobtracker.service;

import com.jobtracker.entity.JobApplication;
import com.jobtracker.entity.User;
import com.jobtracker.exception.ResourceNotFoundException;
import com.jobtracker.repository.ApplicationRepository;
import com.jobtracker.repository.UserRepository;
import com.opencsv.CSVWriter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.StringWriter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CsvExportService {

    private final ApplicationRepository applicationRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public String exportApplicationsToCsv(String email) throws IOException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        List<JobApplication> applications =
                applicationRepository.findByUserAndDeletedFalseOrderByCreatedAtDesc(user);

        StringWriter stringWriter = new StringWriter();

        try (CSVWriter csvWriter = new CSVWriter(stringWriter)) {

            String[] header = {
                    "ID", "Company", "Job Title", "Location",
                    "Status", "Source", "Applied Date",
                    "Follow Up Date", "Salary Expectation",
                    "Interview Rounds", "Notes", "Job URL", "Created At"
            };
            csvWriter.writeNext(header);

            for (JobApplication app : applications) {
                String[] row = {
                        String.valueOf(app.getId()),
                        app.getCompanyName(),
                        app.getJobTitle(),
                        app.getLocation() != null ? app.getLocation() : "",
                        app.getStatus().name(),
                        app.getSource() != null ? app.getSource().name() : "",
                        app.getAppliedDate() != null ? app.getAppliedDate().toString() : "",
                        app.getFollowUpDate() != null ? app.getFollowUpDate().toString() : "",
                        app.getSalaryExpectation() != null
                                ? String.valueOf(app.getSalaryExpectation()) : "",
                        String.valueOf(app.getInterviewRounds().size()),
                        app.getNotes() != null ? app.getNotes() : "",
                        app.getJobUrl() != null ? app.getJobUrl() : "",
                        app.getCreatedAt() != null ? app.getCreatedAt().toString() : ""
                };
                csvWriter.writeNext(row);
            }
        }

        return stringWriter.toString();
    }
}