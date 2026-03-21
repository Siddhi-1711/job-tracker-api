package com.jobtracker.service;

import com.jobtracker.dto.auth.LoginRequest;
import com.jobtracker.dto.auth.RegisterRequest;
import com.jobtracker.dto.auth.AuthResponse;
import com.jobtracker.entity.RefreshToken;
import com.jobtracker.entity.User;
import com.jobtracker.exception.TokenException;
import com.jobtracker.repository.RefreshTokenRepository;
import com.jobtracker.repository.UserRepository;
import com.jobtracker.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthService Unit Tests")
class AuthServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private RefreshTokenRepository refreshTokenRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private JwtUtil jwtUtil;
    @Mock private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthService authService;

    private User testUser;
    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1L)
                .name("Siddhi")
                .email("siddhi@gmail.com")
                .password("encodedPassword")
                .role("ROLE_USER")
                .build();

        registerRequest = new RegisterRequest();
        registerRequest.setName("Siddhi");
        registerRequest.setEmail("siddhi@gmail.com");
        registerRequest.setPassword("password123");

        loginRequest = new LoginRequest();
        loginRequest.setEmail("siddhi@gmail.com");
        loginRequest.setPassword("password123");
    }

    @Test
    @DisplayName("Should register user successfully")
    void shouldRegisterUserSuccessfully() {
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(jwtUtil.generateAccessToken(anyString())).thenReturn("accessToken");
        when(jwtUtil.generateRefreshToken(anyString())).thenReturn("refreshToken");
        when(refreshTokenRepository.findByUser(any())).thenReturn(Optional.empty());
        when(refreshTokenRepository.save(any())).thenReturn(new RefreshToken());

        AuthResponse response = authService.register(registerRequest);

        assertThat(response).isNotNull();
        assertThat(response.getEmail()).isEqualTo("siddhi@gmail.com");
        assertThat(response.getName()).isEqualTo("Siddhi");
        assertThat(response.getAccessToken()).isEqualTo("accessToken");
        assertThat(response.getRefreshToken()).isEqualTo("refreshToken");
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("Should throw exception when email already registered")
    void shouldThrowExceptionWhenEmailAlreadyExists() {
        when(userRepository.existsByEmail("siddhi@gmail.com")).thenReturn(true);

        assertThatThrownBy(() -> authService.register(registerRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Email already registered");

        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should login successfully with valid credentials")
    void shouldLoginSuccessfully() {
        when(authenticationManager.authenticate(any())).thenReturn(null);
        when(userRepository.findByEmail("siddhi@gmail.com"))
                .thenReturn(Optional.of(testUser));
        when(jwtUtil.generateAccessToken(anyString())).thenReturn("accessToken");
        when(jwtUtil.generateRefreshToken(anyString())).thenReturn("refreshToken");
        when(refreshTokenRepository.findByUser(any())).thenReturn(Optional.empty());
        when(refreshTokenRepository.save(any())).thenReturn(new RefreshToken());

        AuthResponse response = authService.login(loginRequest);

        assertThat(response).isNotNull();
        assertThat(response.getAccessToken()).isEqualTo("accessToken");
        verify(authenticationManager, times(1)).authenticate(
                any(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    @DisplayName("Should throw BadCredentialsException on invalid login")
    void shouldThrowExceptionOnInvalidLogin() {
        when(authenticationManager.authenticate(any()))
                .thenThrow(new BadCredentialsException("Invalid credentials"));

        assertThatThrownBy(() -> authService.login(loginRequest))
                .isInstanceOf(BadCredentialsException.class);
    }

    @Test
    @DisplayName("Should throw TokenException when refresh token not found")
    void shouldThrowExceptionWhenRefreshTokenNotFound() {
        when(refreshTokenRepository.findByToken(anyString()))
                .thenReturn(Optional.empty());

        var request = new com.jobtracker.dto.auth.RefreshTokenRequest();
        request.setRefreshToken("invalidToken");

        assertThatThrownBy(() -> authService.refreshToken(request))
                .isInstanceOf(TokenException.class)
                .hasMessageContaining("Refresh token not found");
    }

    @Test
    @DisplayName("Should throw TokenException when refresh token expired")
    void shouldThrowExceptionWhenRefreshTokenExpired() {
        RefreshToken expiredToken = RefreshToken.builder()
                .token("expiredToken")
                .user(testUser)
                .expiryDate(Instant.now().minusSeconds(3600))
                .build();

        when(refreshTokenRepository.findByToken("expiredToken"))
                .thenReturn(Optional.of(expiredToken));

        var request = new com.jobtracker.dto.auth.RefreshTokenRequest();
        request.setRefreshToken("expiredToken");

        assertThatThrownBy(() -> authService.refreshToken(request))
                .isInstanceOf(TokenException.class)
                .hasMessageContaining("expired");
    }
}