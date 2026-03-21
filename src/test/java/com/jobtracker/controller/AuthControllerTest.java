package com.jobtracker.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jobtracker.BaseIntegrationTest;
import com.jobtracker.dto.auth.AuthResponse;
import com.jobtracker.dto.auth.LoginRequest;
import com.jobtracker.dto.auth.RegisterRequest;
import com.jobtracker.service.AuthService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@DisplayName("AuthController Tests")
class AuthControllerTest extends BaseIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @MockitoBean private AuthService authService;

    @Test
    @DisplayName("POST /api/v1/auth/register — should return 201 with tokens")
    void shouldRegisterSuccessfully() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setName("Siddhi");
        request.setEmail("siddhi@gmail.com");
        request.setPassword("password123");

        AuthResponse response = AuthResponse.builder()
                .accessToken("accessToken")
                .refreshToken("refreshToken")
                .tokenType("Bearer")
                .email("siddhi@gmail.com")
                .name("Siddhi")
                .build();

        when(authService.register(any())).thenReturn(response);

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.accessToken").value("accessToken"))
                .andExpect(jsonPath("$.data.email").value("siddhi@gmail.com"))
                .andExpect(jsonPath("$.data.tokenType").value("Bearer"));
    }

    @Test
    @DisplayName("POST /api/v1/auth/register — should return 400 when email invalid")
    void shouldReturn400WhenEmailInvalid() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setName("Siddhi");
        request.setEmail("not-an-email");
        request.setPassword("password123");

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.data.email").exists());
    }

    @Test
    @DisplayName("POST /api/v1/auth/register — should return 400 when password too short")
    void shouldReturn400WhenPasswordTooShort() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setName("Siddhi");
        request.setEmail("siddhi@gmail.com");
        request.setPassword("abc");

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.data.password").exists());
    }

    @Test
    @DisplayName("POST /api/v1/auth/login — should return 200 with tokens")
    void shouldLoginSuccessfully() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setEmail("siddhi@gmail.com");
        request.setPassword("password123");

        AuthResponse response = AuthResponse.builder()
                .accessToken("accessToken")
                .refreshToken("refreshToken")
                .tokenType("Bearer")
                .email("siddhi@gmail.com")
                .name("Siddhi")
                .build();

        when(authService.login(any())).thenReturn(response);

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.accessToken").value("accessToken"))
                .andExpect(jsonPath("$.data.email").value("siddhi@gmail.com"));
    }

    @Test
    @DisplayName("POST /api/v1/auth/login — should return 400 when body is empty")
    void shouldReturn400WhenBodyEmpty() throws Exception {
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }
}