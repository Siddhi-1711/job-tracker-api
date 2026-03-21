package com.jobtracker.repository;

import com.jobtracker.entity.ApplicationStatusHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StatusHistoryRepository extends JpaRepository<ApplicationStatusHistory, Long> {

    List<ApplicationStatusHistory> findByApplicationIdOrderByChangedAtAsc(Long applicationId);
}