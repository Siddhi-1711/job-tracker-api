package com.jobtracker.service;

import com.jobtracker.dto.application.ApplicationRequest;
import com.jobtracker.dto.application.ApplicationResponse;
import com.jobtracker.entity.JobApplication;
import com.jobtracker.entity.User;
import com.jobtracker.enums.ApplicationSource;
import com.jobtracker.enums.ApplicationStatus;
import com.jobtracker.exception.ResourceNotFoundException;
import com.jobtracker.repository.ApplicationRepository;
import com.jobtracker.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ApplicationService Unit Tests")
class ApplicationServiceTest {

    @Mock private ApplicationRepository applicationRepository;
    @Mock private UserRepository userRepository;
    @Mock private com.jobtracker.repository.StatusHistoryRepository statusHistoryRepository;
    @InjectMocks
    private ApplicationService applicationService;

    private User testUser;
    private JobApplication testApplication;
    private ApplicationRequest applicationRequest;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1L)
                .name("Siddhi")
                .email("siddhi@gmail.com")
                .password("encodedPassword")
                .role("ROLE_USER")
                .build();

        testApplication = JobApplication.builder()
                .id(1L)
                .companyName("Google")
                .jobTitle("Backend Engineer")
                .location("Bangalore")
                .status(ApplicationStatus.APPLIED)
                .source(ApplicationSource.LINKEDIN)
                .appliedDate(LocalDate.now())
                .user(testUser)
                .interviewRounds(new ArrayList<>())
                .build();

        applicationRequest = new ApplicationRequest();
        applicationRequest.setCompanyName("Google");
        applicationRequest.setJobTitle("Backend Engineer");
        applicationRequest.setLocation("Bangalore");
        applicationRequest.setStatus(ApplicationStatus.APPLIED);
        applicationRequest.setSource(ApplicationSource.LINKEDIN);
        applicationRequest.setAppliedDate(LocalDate.now());
    }

    @Test
    @DisplayName("Should create application successfully")
    void shouldCreateApplicationSuccessfully() {
        when(userRepository.findByEmail("siddhi@gmail.com"))
                .thenReturn(Optional.of(testUser));
        when(applicationRepository.save(any(JobApplication.class)))
                .thenReturn(testApplication);

        ApplicationResponse response = applicationService.create(
                applicationRequest, "siddhi@gmail.com");

        assertThat(response).isNotNull();
        assertThat(response.getCompanyName()).isEqualTo("Google");
        assertThat(response.getJobTitle()).isEqualTo("Backend Engineer");
        assertThat(response.getStatus()).isEqualTo(ApplicationStatus.APPLIED);
        verify(applicationRepository, times(1)).save(any(JobApplication.class));
    }

    @Test
    @DisplayName("Should return all applications for user")
    void shouldReturnAllApplicationsForUser() {
        when(userRepository.findByEmail("siddhi@gmail.com"))
                .thenReturn(Optional.of(testUser));
        when(applicationRepository.findByUserAndDeletedFalseOrderByCreatedAtDesc(testUser))
                .thenReturn(List.of(testApplication));

        List<ApplicationResponse> responses = applicationService.getAll(
                "siddhi@gmail.com", null);

        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).getCompanyName()).isEqualTo("Google");
    }

    @Test
    @DisplayName("Should return applications filtered by status")
    void shouldReturnApplicationsFilteredByStatus() {
        when(userRepository.findByEmail("siddhi@gmail.com"))
                .thenReturn(Optional.of(testUser));
        when(applicationRepository.findByUserAndStatusAndDeletedFalseOrderByCreatedAtDesc(
                testUser, ApplicationStatus.APPLIED))
                .thenReturn(List.of(testApplication));

        List<ApplicationResponse> responses = applicationService.getAll(
                "siddhi@gmail.com", ApplicationStatus.APPLIED);

        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).getStatus()).isEqualTo(ApplicationStatus.APPLIED);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when application not found")
    void shouldThrowExceptionWhenApplicationNotFound() {
        when(applicationRepository.findByIdAndUserEmail(99L, "siddhi@gmail.com"))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> applicationService.getById(99L, "siddhi@gmail.com"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Application not found");
    }

    @Test
    @DisplayName("Should update application status successfully")
    void shouldUpdateApplicationStatusSuccessfully() {
        when(applicationRepository.findByIdAndUserEmail(1L, "siddhi@gmail.com"))
                .thenReturn(Optional.of(testApplication));

        testApplication.setStatus(ApplicationStatus.SCREENING);
        when(applicationRepository.save(any(JobApplication.class)))
                .thenReturn(testApplication);

        ApplicationResponse response = applicationService.updateStatus(
                1L, ApplicationStatus.SCREENING, "siddhi@gmail.com");

        assertThat(response.getStatus()).isEqualTo(ApplicationStatus.SCREENING);
        verify(applicationRepository, times(1)).save(any(JobApplication.class));
    }

    @Test
    @DisplayName("Should delete application successfully")
    void shouldDeleteApplicationSuccessfully() {
        when(applicationRepository.findByIdAndUserEmail(1L, "siddhi@gmail.com"))
                .thenReturn(Optional.of(testApplication));

        applicationService.delete(1L, "siddhi@gmail.com");

        verify(applicationRepository, times(1)).save(any(JobApplication.class));
        assertThat(testApplication.getDeleted()).isTrue();
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when user not found")
    void shouldThrowExceptionWhenUserNotFound() {
        when(userRepository.findByEmail("unknown@gmail.com"))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> applicationService.getAll(
                "unknown@gmail.com", null))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("User not found");
    }
}