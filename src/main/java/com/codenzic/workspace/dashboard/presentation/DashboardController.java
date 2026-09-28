package com.codenzic.workspace.dashboard.presentation;

import com.codenzic.workspace.common.api.ApiResponse;
import com.codenzic.workspace.dashboard.application.DashboardService;
import com.codenzic.workspace.dashboard.presentation.dto.DashboardDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/dashboard")
@RequiredArgsConstructor
@Tag(name = "Dashboard", description = "Dashboard API operations.")
@SecurityRequirement(name = "bearerAuth")
public class DashboardController {
    private final DashboardService service;

    @GetMapping
    @PreAuthorize("hasAuthority('DASHBOARD_VIEW')")
    @Operation(
            summary = "Get dashboard",
            description = "Returns the current organization's dashboard.",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "200", description = "Dashboard returned."),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "401", description = "Authentication is required."),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
                            responseCode = "403", description = "Dashboard access is denied.")
            })
    public ApiResponse<DashboardDto> get() {
        return ApiResponse.success(service.get(), "/api/v1/dashboard");
    }
}
