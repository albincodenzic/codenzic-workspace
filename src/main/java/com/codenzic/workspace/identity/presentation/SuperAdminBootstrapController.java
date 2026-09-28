package com.codenzic.workspace.identity.presentation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

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
@Tag(name = "Auth", description = "Auth API operations.")
public class SuperAdminBootstrapController {
    private final SuperAdminBootstrapService bootstrapService;

    @Operation(summary = "Bootstrap", description = "Public authentication endpoint.", responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Request completed successfully."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Request validation failed or a parameter is invalid."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Authentication is required or credentials are invalid."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "The authenticated user is not allowed to perform this operation."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "The requested resource was not found."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "The request conflicts with the current resource state.")
    })
    @PostMapping("/bootstrap/super-admin")
    public ResponseEntity<ApiResponse<SuperAdminBootstrapResponse>> bootstrap(
            @Parameter(description = "Request header value.") @RequestHeader(name = "X-Super-Admin-Bootstrap-Secret", required = false)
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