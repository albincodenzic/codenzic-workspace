package com.codenzic.workspace.identity.presentation;

import com.codenzic.workspace.common.api.ApiResponse;
import com.codenzic.workspace.identity.application.AuthService;
import com.codenzic.workspace.identity.presentation.dto.AuthResponse;
import com.codenzic.workspace.identity.presentation.dto.CurrentUserResponse;
import com.codenzic.workspace.identity.presentation.dto.LoginRequest;
import com.codenzic.workspace.identity.presentation.dto.RefreshRequest;
import com.codenzic.workspace.identity.presentation.dto.RegisterRequest;
import com.codenzic.workspace.identity.domain.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/register")
    @PreAuthorize("hasAuthority('EMPLOYEE_CREATE')")
    ResponseEntity<ApiResponse<AuthResponse>> register(
            @Valid @RequestBody RegisterRequest request,
            HttpServletRequest httpRequest
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(authService.register(request), httpRequest.getRequestURI()));
    }

    @PostMapping("/login")
    ResponseEntity<ApiResponse<AuthResponse>> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletRequest httpRequest
    ) {
        return ResponseEntity.ok(ApiResponse.success(authService.login(request), httpRequest.getRequestURI()));
    }

    @PostMapping("/refresh")
    ResponseEntity<ApiResponse<AuthResponse>> refresh(
            @Valid @RequestBody RefreshRequest request,
            HttpServletRequest httpRequest
    ) {
        return ResponseEntity.ok(ApiResponse.success(authService.refresh(request), httpRequest.getRequestURI()));
    }

    @PostMapping("/logout")
    ResponseEntity<ApiResponse<Void>> logout(
            @Valid @RequestBody RefreshRequest request,
            HttpServletRequest httpRequest
    ) {
        authService.logout(request);
        return ResponseEntity.ok(ApiResponse.success(null, httpRequest.getRequestURI()));
    }

    @org.springframework.web.bind.annotation.GetMapping("/me")
    ResponseEntity<ApiResponse<CurrentUserResponse>> me(
            @AuthenticationPrincipal User user,
            HttpServletRequest httpRequest
    ) {
        return ResponseEntity.ok(ApiResponse.success(authService.me(user), httpRequest.getRequestURI()));
    }
}
