package com.jobtracker;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jobtracker.dto.application.ApplicationRequest;
import com.jobtracker.dto.auth.LoginRequest;
import com.jobtracker.dto.auth.RegisterRequest;
import com.jobtracker.enums.ApplicationSource;
import com.jobtracker.enums.ApplicationStatus;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDate;
import java.util.TimeZone;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@DisplayName("Application Integration Tests")
class ApplicationIntegrationTest extends BaseIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    private String accessToken;

    @BeforeAll
    static void setTimezone() {
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Kolkata"));
    }

    @BeforeEach
    void setUp() throws Exception {
        RegisterRequest register = new RegisterRequest();
        register.setName("Siddhi");
        register.setEmail("siddhi@test.com");
        register.setPassword("password123");

        mockMvc.perform(post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(register)));

        LoginRequest login = new LoginRequest();
        login.setEmail("siddhi@test.com");
        login.setPassword("password123");

        MvcResult result = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(login)))
                .andReturn();

        String body = result.getResponse().getContentAsString();
        accessToken = objectMapper.readTree(body)
                .path("data")
                .path("accessToken")
                .asText();
    }

    @Test
    @DisplayName("Should create and retrieve application")
    void shouldCreateAndRetrieveApplication() throws Exception {
        ApplicationRequest request = new ApplicationRequest();
        request.setCompanyName("Google");
        request.setJobTitle("Backend Engineer");
        request.setLocation("Bangalore");
        request.setStatus(ApplicationStatus.APPLIED);
        request.setSource(ApplicationSource.LINKEDIN);
        request.setAppliedDate(LocalDate.now());

        MvcResult createResult = mockMvc.perform(post("/api/v1/applications")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.companyName").value("Google"))
                .andExpect(jsonPath("$.data.status").value("APPLIED"))
                .andReturn();

        Long id = objectMapper.readTree(createResult.getResponse().getContentAsString())
                .path("data").path("id").asLong();

        mockMvc.perform(get("/api/v1/applications/" + id)
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.companyName").value("Google"))
                .andExpect(jsonPath("$.data.jobTitle").value("Backend Engineer"));
    }

    @Test
    @DisplayName("Should return 4xx when accessing protected endpoint without token")
    void shouldReturn401WhenNoToken() throws Exception {
        mockMvc.perform(get("/api/v1/applications"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @DisplayName("Should not access another user's application")
    void shouldNotAccessOtherUsersApplication() throws Exception {
        ApplicationRequest request = new ApplicationRequest();
        request.setCompanyName("Google");
        request.setJobTitle("Backend Engineer");
        request.setLocation("Bangalore");
        request.setStatus(ApplicationStatus.APPLIED);
        request.setSource(ApplicationSource.LINKEDIN);

        MvcResult createResult = mockMvc.perform(post("/api/v1/applications")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn();

        Long id = objectMapper.readTree(createResult.getResponse().getContentAsString())
                .path("data").path("id").asLong();

        RegisterRequest otherUser = new RegisterRequest();
        otherUser.setName("Other User");
        otherUser.setEmail("other@test.com");
        otherUser.setPassword("password123");

        mockMvc.perform(post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(otherUser)));

        LoginRequest otherLogin = new LoginRequest();
        otherLogin.setEmail("other@test.com");
        otherLogin.setPassword("password123");

        MvcResult otherResult = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(otherLogin)))
                .andReturn();

        String otherToken = objectMapper.readTree(
                        otherResult.getResponse().getContentAsString())
                .path("data").path("accessToken").asText();

        mockMvc.perform(get("/api/v1/applications/" + id)
                        .header("Authorization", "Bearer " + otherToken))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should update application status")
    void shouldUpdateApplicationStatus() throws Exception {
        ApplicationRequest request = new ApplicationRequest();
        request.setCompanyName("Microsoft");
        request.setJobTitle("Java Developer");
        request.setStatus(ApplicationStatus.APPLIED);
        request.setSource(ApplicationSource.NAUKRI);

        MvcResult createResult = mockMvc.perform(post("/api/v1/applications")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn();

        Long id = objectMapper.readTree(createResult.getResponse().getContentAsString())
                .path("data").path("id").asLong();

        mockMvc.perform(patch("/api/v1/applications/" + id + "/status")
                        .header("Authorization", "Bearer " + accessToken)
                        .param("status", "INTERVIEW"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("INTERVIEW"));
    }

    @Test
    @DisplayName("Should delete application successfully")
    void shouldDeleteApplication() throws Exception {
        ApplicationRequest request = new ApplicationRequest();
        request.setCompanyName("Amazon");
        request.setJobTitle("SDE");
        request.setStatus(ApplicationStatus.APPLIED);
        request.setSource(ApplicationSource.COMPANY_SITE);

        MvcResult createResult = mockMvc.perform(post("/api/v1/applications")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn();

        Long id = objectMapper.readTree(createResult.getResponse().getContentAsString())
                .path("data").path("id").asLong();

        mockMvc.perform(get("/api/v1/applications/" + id)
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk());

        mockMvc.perform(delete("/api/v1/applications/" + id)
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/v1/applications/" + id)
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isNotFound());
    }
}