package com.jobtracker.repository;

import com.jobtracker.entity.InterviewRound;
import com.jobtracker.entity.JobApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface InterviewRoundRepository extends JpaRepository<InterviewRound, Long> {

    List<InterviewRound> findByJobApplicationOrderByRoundNumberAsc(JobApplication jobApplication);

    Optional<InterviewRound> findByIdAndJobApplication(Long id, JobApplication jobApplication);

    @Query("SELECT ir FROM InterviewRound ir WHERE " +
            "ir.scheduledAt BETWEEN :start AND :end AND " +
            "ir.reminderSent = false")
    List<InterviewRound> findUpcomingInterviewsForReminder(
            LocalDateTime start, LocalDateTime end);
}