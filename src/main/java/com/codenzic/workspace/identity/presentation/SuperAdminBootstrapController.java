package com.codenzic.workspace.identity.presentation;

import com.codenzic.workspace.common.api.ApiResponse;
import com.codenzic.workspace.identity.application.SuperAdminBootstrapService;
import com.codenzic.workspace.identity.presentation.dto.SuperAdminBootstrapRequest;
import com.codenzic.workspace.identity.presentation.dto.SuperAdminBootstrapResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class SuperAdminBootstrapController {
    private final SuperAdminBootstrapService bootstrapService;

    @PostMapping("/bootstrap/super-admin")
    public ResponseEntity<ApiResponse<SuperAdminBootstrapResponse>> bootstrap(
            @RequestHeader(name = "X-Super-Admin-Bootstrap-Secret", required = false)
            String bootstrapSecret,
            @Valid @RequestBody SuperAdminBootstrapRequest request,
            HttpServletRequest httpRequest
    ) {
        SuperAdminBootstrapResponse result =
                bootstrapService.bootstrap(bootstrapSecret, request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(result, httpRequest.getRequestURI()));
    }
}