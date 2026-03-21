package com.jobtracker.config;

import com.jobtracker.entity.*;
import com.jobtracker.enums.*;
import com.jobtracker.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@Profile("dev")
@RequiredArgsConstructor
@Slf4j
public class DataSeeder implements ApplicationRunner {

    private final UserRepository userRepository;
    private final ApplicationRepository applicationRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(ApplicationArguments args) {
        if (userRepository.existsByEmail("demo@jobtracker.com")) {
            log.info("Seed data already exists, skipping...");
            return;
        }

        User user = User.builder()
                .name("Demo User")
                .email("demo@jobtracker.com")
                .password(passwordEncoder.encode("demo123"))
                .role("ROLE_USER")
                .build();
        userRepository.save(user);

        String[] companies = {"Google", "Microsoft", "Amazon", "Meta", "Netflix"};
        ApplicationStatus[] statuses = {
                ApplicationStatus.APPLIED, ApplicationStatus.SCREENING,
                ApplicationStatus.INTERVIEW, ApplicationStatus.OFFER,
                ApplicationStatus.REJECTED
        };

        for (int i = 0; i < companies.length; i++) {
            applicationRepository.save(JobApplication.builder()
                    .companyName(companies[i])
                    .jobTitle("Backend Engineer")
                    .status(statuses[i])
                    .source(ApplicationSource.LINKEDIN)
                    .appliedDate(LocalDate.now().minusDays(i * 5))
                    .deleted(false)
                    .user(user)
                    .build());
        }

        log.info("Seed data created — login with demo@jobtracker.com / demo123");
    }
}