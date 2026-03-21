package com.jobtracker.service;

import com.jobtracker.dto.application.*;
import com.jobtracker.dto.interview.InterviewRoundResponse;
import com.jobtracker.entity.ApplicationStatusHistory;
import com.jobtracker.entity.JobApplication;
import com.jobtracker.entity.User;
import com.jobtracker.enums.ApplicationSource;
import com.jobtracker.enums.ApplicationStatus;
import com.jobtracker.exception.ResourceNotFoundException;
import com.jobtracker.repository.ApplicationRepository;
import com.jobtracker.repository.StatusHistoryRepository;
import com.jobtracker.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final UserRepository userRepository;
    private final StatusHistoryRepository statusHistoryRepository;
    private User getUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    @Transactional
    public ApplicationResponse create(ApplicationRequest request, String email) {
        User user = getUser(email);

        JobApplication application = JobApplication.builder()
                .companyName(request.getCompanyName())
                .jobTitle(request.getJobTitle())
                .jobUrl(request.getJobUrl())
                .location(request.getLocation())
                .status(request.getStatus() != null ? request.getStatus() : ApplicationStatus.APPLIED)
                .source(request.getSource() != null ? request.getSource() : ApplicationSource.OTHER)
                .appliedDate(request.getAppliedDate())
                .followUpDate(request.getFollowUpDate())
                .notes(request.getNotes())
                .salaryExpectation(request.getSalaryExpectation())
                .user(user)
                .build();

        JobApplication saved = applicationRepository.save(application);

        ApplicationStatusHistory history = ApplicationStatusHistory.builder()
                .oldStatus(null)
                .newStatus(saved.getStatus())
                .note("Application created")
                .application(saved)
                .build();

        statusHistoryRepository.save(history);

        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<ApplicationResponse> getAll(String email, ApplicationStatus status) {
        User user = getUser(email);

        List<JobApplication> applications = status != null
                ? applicationRepository.findByUserAndStatusAndDeletedFalseOrderByCreatedAtDesc(user, status)
                : applicationRepository.findByUserAndDeletedFalseOrderByCreatedAtDesc(user);

        return applications.stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<ApplicationResponse> search(ApplicationSearchRequest request, String email) {
        User user = getUser(email);

        if (request.getKeyword() != null && !request.getKeyword().isBlank()) {
            return applicationRepository
                    .searchByKeyword(user, request.getKeyword().trim())
                    .stream().map(this::toResponse).toList();
        }

        if (request.getSource() != null) {
            return applicationRepository
                    .findByUserAndSource(user, request.getSource())
                    .stream().map(this::toResponse).toList();
        }

        if (request.getStartDate() != null && request.getEndDate() != null) {
            return applicationRepository
                    .findByUserAndDateRange(user, request.getStartDate(), request.getEndDate())
                    .stream().map(this::toResponse).toList();
        }

        if (request.getStatus() != null) {
            return applicationRepository
                    .findByUserAndStatusAndDeletedFalseOrderByCreatedAtDesc(user, request.getStatus())
                    .stream().map(this::toResponse).toList();
        }

        return applicationRepository
                .findByUserAndDeletedFalseOrderByCreatedAtDesc(user)
                .stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public ApplicationResponse getById(Long id, String email) {
        return applicationRepository.findByIdAndUserEmail(id, email)
                .map(this::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Application not found with id: " + id));
    }

    @Transactional
    public ApplicationResponse update(Long id, ApplicationRequest request, String email) {
        JobApplication application = applicationRepository.findByIdAndUserEmail(id, email)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Application not found with id: " + id));

        ApplicationStatus oldStatus = application.getStatus();

        application.setCompanyName(request.getCompanyName());
        application.setJobTitle(request.getJobTitle());
        application.setJobUrl(request.getJobUrl());
        application.setLocation(request.getLocation());
        if (request.getSource() != null) application.setSource(request.getSource());
        application.setAppliedDate(request.getAppliedDate());
        application.setFollowUpDate(request.getFollowUpDate());
        application.setNotes(request.getNotes());
        application.setSalaryExpectation(request.getSalaryExpectation());

        if (request.getStatus() != null && !request.getStatus().equals(oldStatus)) {
            application.setStatus(request.getStatus());
            applicationRepository.save(application);

            ApplicationStatusHistory history = ApplicationStatusHistory.builder()
                    .oldStatus(oldStatus)
                    .newStatus(request.getStatus())
                    .note("Status updated via full update")
                    .application(application)
                    .build();
            statusHistoryRepository.save(history);
        } else {
            applicationRepository.save(application);
        }

        return toResponse(application);
    }

    @Transactional
    public ApplicationResponse updateStatus(Long id, ApplicationStatus status,
                                            String email) {
        return updateStatus(id, status, email, null);
    }

    @Transactional
    public ApplicationResponse updateStatus(Long id, ApplicationStatus status,
                                            String email, String note) {
        JobApplication application = applicationRepository.findByIdAndUserEmail(id, email)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Application not found with id: " + id));

        ApplicationStatus oldStatus = application.getStatus();
        application.setStatus(status);
        applicationRepository.save(application);

        ApplicationStatusHistory history = ApplicationStatusHistory.builder()
                .oldStatus(oldStatus)
                .newStatus(status)
                .note(note)
                .application(application)
                .build();

        statusHistoryRepository.save(history);

        return toResponse(application);
    }
    @Transactional
    public void delete(Long id, String email) {
        JobApplication application = applicationRepository.findByIdAndUserEmail(id, email)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Application not found with id: " + id));
        application.setDeleted(true);
        applicationRepository.save(application);
    }

    @Transactional(readOnly = true)
    public PagedResponse<ApplicationResponse> getAllPaged(
            String email, ApplicationStatus status,
            int page, int size, String sortBy, String sortDir) {

        User user = getUser(email);

        Sort sort = sortDir.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page, size, sort);

        Page<JobApplication> pageResult = status != null
                ? applicationRepository.findByUserAndStatusAndDeletedFalse(user, status, pageable)
                : applicationRepository.findByUserAndDeletedFalse(user, pageable);

        List<ApplicationResponse> content = pageResult.getContent()
                .stream().map(this::toResponse).toList();

        return PagedResponse.<ApplicationResponse>builder()
                .content(content)
                .pageNumber(pageResult.getNumber())
                .pageSize(pageResult.getSize())
                .totalElements(pageResult.getTotalElements())
                .totalPages(pageResult.getTotalPages())
                .first(pageResult.isFirst())
                .last(pageResult.isLast())
                .build();
    }

    public ApplicationResponse toResponse(JobApplication app) {
        List<InterviewRoundResponse> rounds = app.getInterviewRounds().stream()
                .map(round -> InterviewRoundResponse.builder()
                        .id(round.getId())
                        .roundNumber(round.getRoundNumber())
                        .interviewType(round.getInterviewType())
                        .scheduledAt(round.getScheduledAt())
                        .interviewerName(round.getInterviewerName())
                        .meetingLink(round.getMeetingLink())
                        .notes(round.getNotes())
                        .outcome(round.getOutcome())
                        .reminderSent(round.getReminderSent())
                        .createdAt(round.getCreatedAt())
                        .build())
                .toList();

        return ApplicationResponse.builder()
                .id(app.getId())
                .companyName(app.getCompanyName())
                .jobTitle(app.getJobTitle())
                .jobUrl(app.getJobUrl())
                .location(app.getLocation())
                .status(app.getStatus())
                .source(app.getSource())
                .appliedDate(app.getAppliedDate())
                .followUpDate(app.getFollowUpDate())
                .notes(app.getNotes())
                .salaryExpectation(app.getSalaryExpectation())
                .createdAt(app.getCreatedAt())
                .updatedAt(app.getUpdatedAt())
                .interviewRounds(rounds)
                .build();
    }
    @Transactional(readOnly = true)
    public List<StatusHistoryResponse> getStatusHistory(Long id, String email) {
        applicationRepository.findByIdAndUserEmail(id, email)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Application not found with id: " + id));

        return statusHistoryRepository
                .findByApplicationIdOrderByChangedAtAsc(id)
                .stream()
                .map(h -> StatusHistoryResponse.builder()
                        .id(h.getId())
                        .oldStatus(h.getOldStatus())
                        .newStatus(h.getNewStatus())
                        .changedAt(h.getChangedAt())
                        .note(h.getNote())
                        .build())
                .toList();
    }
}