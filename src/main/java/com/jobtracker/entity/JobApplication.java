package com.jobtracker.entity;

import com.jobtracker.enums.ApplicationSource;
import com.jobtracker.enums.ApplicationStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "job_applications")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String companyName;

    @Column(nullable = false)
    private String jobTitle;

    private String jobUrl;

    private String location;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private ApplicationStatus status = ApplicationStatus.APPLIED;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private ApplicationSource source = ApplicationSource.OTHER;

    private LocalDate appliedDate;

    private LocalDate followUpDate;

    @Column(columnDefinition = "TEXT")
    private String notes;

    private Integer salaryExpectation;
    private String referralPerson;

    private String recruiterName;

    private String recruiterEmail;

    private String recruiterPhone;

    private String resumeVersion;

    private String jdUrl;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "jobApplication", cascade = CascadeType.ALL, orphanRemoval = true,  fetch = FetchType.LAZY)
    @Builder.Default
    private List<InterviewRound> interviewRounds = new ArrayList<>();

    @OneToMany(mappedBy = "application", cascade = CascadeType.ALL,
            orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<ApplicationStatusHistory> statusHistory = new ArrayList<>();

    @Column(nullable = false)
    @Builder.Default
    private Boolean deleted = false;
}