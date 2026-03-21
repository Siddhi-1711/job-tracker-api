package com.jobtracker.repository;

import com.jobtracker.entity.JobApplication;
import com.jobtracker.entity.User;
import com.jobtracker.enums.ApplicationSource;
import com.jobtracker.enums.ApplicationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ApplicationRepository extends JpaRepository<JobApplication, Long> {


        // ── List queries (used by getAll, search, analytics, csv) ──
        List<JobApplication> findByUserAndDeletedFalseOrderByCreatedAtDesc(User user);

        List<JobApplication> findByUserAndStatusAndDeletedFalseOrderByCreatedAtDesc(
                User user, ApplicationStatus status);

        // ── Pageable queries (used by getAllPaged) ──
        @Query("SELECT a FROM JobApplication a WHERE a.user = :user " +
                "AND a.deleted = false")
        Page<JobApplication> findByUserAndDeletedFalse(
                @Param("user") User user, Pageable pageable);

        @Query("SELECT a FROM JobApplication a WHERE a.user = :user " +
                "AND a.deleted = false AND a.status = :status")
        Page<JobApplication> findByUserAndStatusAndDeletedFalse(
                @Param("user") User user,
                @Param("status") ApplicationStatus status,
                Pageable pageable);

        // ── Single lookup ──
        @Query("SELECT a FROM JobApplication a WHERE a.id = :id " +
                "AND a.user.email = :email AND a.deleted = false")
        Optional<JobApplication> findByIdAndUserEmail(@Param("id") Long id,
                                                      @Param("email") String email);

        // ── Count queries (used by analytics) ──
        long countByUserAndStatusAndDeletedFalse(User user, ApplicationStatus status);

        long countByUserAndDeletedFalse(User user);

        // ── Search queries ──
        @Query("SELECT a FROM JobApplication a WHERE a.user = :user " +
                "AND a.deleted = false AND " +
                "(LOWER(a.companyName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
                "LOWER(a.jobTitle) LIKE LOWER(CONCAT('%', :keyword, '%')))")
        List<JobApplication> searchByKeyword(@Param("user") User user,
                                             @Param("keyword") String keyword);

        @Query("SELECT a FROM JobApplication a WHERE a.user = :user " +
                "AND a.deleted = false AND a.source = :source ORDER BY a.createdAt DESC")
        List<JobApplication> findByUserAndSource(@Param("user") User user,
                                                 @Param("source") ApplicationSource source);

        @Query("SELECT a FROM JobApplication a WHERE a.user = :user " +
                "AND a.deleted = false AND " +
                "a.appliedDate BETWEEN :startDate AND :endDate ORDER BY a.appliedDate DESC")
        List<JobApplication> findByUserAndDateRange(@Param("user") User user,
                                                    @Param("startDate") LocalDate startDate,
                                                    @Param("endDate") LocalDate endDate);

        // ── Scheduler query ──
        @Query("SELECT a FROM JobApplication a WHERE a.user.id IS NOT NULL " +
                "AND a.deleted = false AND a.followUpDate = :today " +
                "AND a.status NOT IN ('OFFER', 'REJECTED', 'WITHDRAWN')")
        List<JobApplication> findApplicationsDueForFollowUp(@Param("today") LocalDate today);


}