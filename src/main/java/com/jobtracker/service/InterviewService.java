package com.jobtracker.service;

import com.jobtracker.dto.interview.InterviewRoundRequest;
import com.jobtracker.dto.interview.InterviewRoundResponse;
import com.jobtracker.entity.InterviewRound;
import com.jobtracker.entity.JobApplication;
import com.jobtracker.entity.User;
import com.jobtracker.exception.ResourceNotFoundException;
import com.jobtracker.repository.ApplicationRepository;
import com.jobtracker.repository.InterviewRoundRepository;
import com.jobtracker.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InterviewService {

    private final InterviewRoundRepository interviewRoundRepository;
    private final ApplicationRepository applicationRepository;
    private final UserRepository userRepository;

    private User getUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    private JobApplication getApplication(Long applicationId, User user) {
        return applicationRepository.findByIdAndUserEmail(applicationId, user.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Application not found with id: " + applicationId));
    }
    @Transactional
    public InterviewRoundResponse addRound(Long applicationId,
                                           InterviewRoundRequest request,
                                           String email) {
        User user = getUser(email);
        JobApplication application = getApplication(applicationId, user);

        InterviewRound round = InterviewRound.builder()
                .roundNumber(request.getRoundNumber())
                .interviewType(request.getInterviewType())
                .scheduledAt(request.getScheduledAt())
                .interviewerName(request.getInterviewerName())
                .meetingLink(request.getMeetingLink())
                .notes(request.getNotes())
                .outcome(request.getOutcome())
                .reminderSent(false)
                .jobApplication(application)
                .build();

        return toResponse(interviewRoundRepository.save(round));
    }

    public List<InterviewRoundResponse> getRounds(Long applicationId, String email) {
        User user = getUser(email);
        JobApplication application = getApplication(applicationId, user);
        return interviewRoundRepository
                .findByJobApplicationOrderByRoundNumberAsc(application)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public InterviewRoundResponse updateRound(Long applicationId,
                                              Long roundId,
                                              InterviewRoundRequest request,
                                              String email) {
        User user = getUser(email);
        JobApplication application = getApplication(applicationId, user);

        InterviewRound round = interviewRoundRepository
                .findByIdAndJobApplication(roundId, application)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Interview round not found with id: " + roundId));

        round.setRoundNumber(request.getRoundNumber());
        round.setInterviewType(request.getInterviewType());
        round.setScheduledAt(request.getScheduledAt());
        round.setInterviewerName(request.getInterviewerName());
        round.setMeetingLink(request.getMeetingLink());
        round.setNotes(request.getNotes());
        round.setOutcome(request.getOutcome());

        return toResponse(interviewRoundRepository.save(round));
    }

    @Transactional
    public void deleteRound(Long applicationId, Long roundId, String email) {
        User user = getUser(email);
        JobApplication application = getApplication(applicationId, user);

        InterviewRound round = interviewRoundRepository
                .findByIdAndJobApplication(roundId, application)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Interview round not found with id: " + roundId));

        interviewRoundRepository.delete(round);
    }

    public InterviewRoundResponse toResponse(InterviewRound round) {
        return InterviewRoundResponse.builder()
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
                .build();
    }
}