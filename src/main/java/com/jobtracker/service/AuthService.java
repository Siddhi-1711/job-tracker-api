package com.jobtracker.service;

import com.jobtracker.dto.auth.*;
import com.jobtracker.entity.RefreshToken;
import com.jobtracker.entity.User;
import com.jobtracker.exception.TokenException;
import com.jobtracker.repository.RefreshTokenRepository;
import com.jobtracker.repository.UserRepository;
import com.jobtracker.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    @Value("${app.jwt.refresh-token-expiration}")
    private long refreshTokenExpiration;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already registered: " + request.getEmail());
        }

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .build();

        userRepository.save(user);

        String accessToken = jwtUtil.generateAccessToken(user.getEmail());
        String refreshToken = createRefreshToken(user);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .email(user.getEmail())
                .name(user.getName())
                .build();
    }

    @Transactional
    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        String accessToken = jwtUtil.generateAccessToken(user.getEmail());
        String refreshToken = createRefreshToken(user);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .email(user.getEmail())
                .name(user.getName())
                .build();
    }

    @Transactional
    public AuthResponse refreshToken(RefreshTokenRequest request) {
        RefreshToken refreshToken = refreshTokenRepository
                .findByToken(request.getRefreshToken())
                .orElseThrow(() -> new TokenException("Refresh token not found"));

        if (refreshToken.getExpiryDate().isBefore(Instant.now())) {
            refreshTokenRepository.delete(refreshToken);
            throw new TokenException("Refresh token expired. Please login again");
        }

        User user = refreshToken.getUser();
        String newAccessToken = jwtUtil.generateAccessToken(user.getEmail());
        String newRefreshToken = createRefreshToken(user);

        return AuthResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .tokenType("Bearer")
                .email(user.getEmail())
                .name(user.getName())
                .build();
    }

    @Transactional
    public void logout(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        refreshTokenRepository.deleteByUser(user);
    }

    private String createRefreshToken(User user) {
        refreshTokenRepository.findByUser(user)
                .ifPresent(refreshTokenRepository::delete);

        refreshTokenRepository.flush();

        String tokenValue = jwtUtil.generateRefreshToken(user.getEmail());

        RefreshToken refreshToken = RefreshToken.builder()
                .token(tokenValue)
                .user(user)
                .expiryDate(Instant.now().plusMillis(refreshTokenExpiration))
                .build();

        refreshTokenRepository.save(refreshToken);
        return tokenValue;
    }
}