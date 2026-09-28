package com.codenzic.workspace.audit.presentation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import com.codenzic.workspace.audit.application.AuditLogService;
import com.codenzic.workspace.audit.presentation.dto.AuditLogResponse;
import com.codenzic.workspace.common.api.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/audit-logs")
@RequiredArgsConstructor
@Tag(name = "Audit", description = "Audit API operations.")
@SecurityRequirement(name = "bearerAuth")
public class AuditLogController {
    private final AuditLogService service;

    @Operation(summary = "List", description = "Requires a valid JWT and any authority enforced by the endpoint.", responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Request completed successfully."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Request validation failed or a parameter is invalid."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Authentication is required or credentials are invalid."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "The authenticated user is not allowed to perform this operation."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "The requested resource was not found."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "The request conflicts with the current resource state.")
    })
    @GetMapping
    @PreAuthorize("hasAuthority('AUDIT_LOG_READ')")
    public ApiResponse<List<AuditLogResponse>> list(
            @Parameter(description = "Request filter or option.") @RequestParam(defaultValue = "50") int limit,
            @Parameter(description = "Request filter or option.") @RequestParam(required = false) UUID organizationId
    ) {
        return ApiResponse.success(service.list(limit, organizationId), "/api/v1/audit-logs");
    }
}
