package com.jobtracker.entity;

import com.jobtracker.enums.InterviewType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "interview_rounds")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InterviewRound {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer roundNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InterviewType interviewType;

    @Column(nullable = false)
    private LocalDateTime scheduledAt;

    private String interviewerName;

    private String meetingLink;

    @Column(columnDefinition = "TEXT")
    private String notes;

    private String outcome;
    @Column(columnDefinition = "TEXT")
    private String prepNotes;

    private Boolean reminderSent = false;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_application_id", nullable = false)
    private JobApplication jobApplication;
}