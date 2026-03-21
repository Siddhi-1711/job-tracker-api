package com.jobtracker.controller;

import com.jobtracker.dto.ApiResponse;
import com.jobtracker.dto.auth.*;
import com.jobtracker.service.AuthService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(
            @Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("User registered successfully",
                        authService.register(request)));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(
            @Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(
                ApiResponse.success("Login successful", authService.login(request)));
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<AuthResponse>> refresh(
            @Valid @RequestBody RefreshTokenRequest request) {
        return ResponseEntity.ok(
                ApiResponse.success("Token refreshed", authService.refreshToken(request)));
    }

    @PostMapping("/logout")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<ApiResponse<Void>> logout(
            @AuthenticationPrincipal UserDetails userDetails) {
        authService.logout(userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success("Logged out successfully", null));
    }
}