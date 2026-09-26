package com.codenzic.workspace.audit.presentation;

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
public class AuditLogController {
    private final AuditLogService service;

    @GetMapping
    @PreAuthorize("hasAuthority('AUDIT_LOG_READ')")
    public ApiResponse<List<AuditLogResponse>> list(
            @RequestParam(defaultValue = "50") int limit,
            @RequestParam(required = false) UUID organizationId
    ) {
        return ApiResponse.success(service.list(limit, organizationId), "/api/v1/audit-logs");
    }
}
