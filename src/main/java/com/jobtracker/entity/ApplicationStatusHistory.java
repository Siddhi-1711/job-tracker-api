package com.jobtracker.entity;

import com.jobtracker.enums.ApplicationStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "application_status_history")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApplicationStatusHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private ApplicationStatus oldStatus;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ApplicationStatus newStatus;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime changedAt;

    @Column(columnDefinition = "TEXT")
    private String note;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "application_id", nullable = false)
    private JobApplication application;
}