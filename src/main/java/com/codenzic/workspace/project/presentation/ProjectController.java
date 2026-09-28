package com.codenzic.workspace.project.presentation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import com.codenzic.workspace.common.api.ApiResponse;
import com.codenzic.workspace.common.api.PageResponse;
import com.codenzic.workspace.project.application.ProjectService;
import com.codenzic.workspace.project.presentation.dto.ProjectMemberRequest;
import com.codenzic.workspace.project.presentation.dto.ProjectMemberResponse;
import com.codenzic.workspace.project.presentation.dto.ProjectRequest;
import com.codenzic.workspace.project.presentation.dto.ProjectResponse;
import com.codenzic.workspace.project.presentation.dto.ProjectStatusRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/projects")
@RequiredArgsConstructor
@Tag(name = "Projects", description = "Projects API operations.")
@SecurityRequirement(name = "bearerAuth")
public class ProjectController {
    private final ProjectService service;

    @Operation(summary = "Create", description = "Requires a valid JWT and any authority enforced by the endpoint.", responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Request completed successfully."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Request validation failed or a parameter is invalid."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Authentication is required or credentials are invalid."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "The authenticated user is not allowed to perform this operation."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "The requested resource was not found."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "The request conflicts with the current resource state.")
    })
    @PostMapping
    @PreAuthorize("hasAuthority('PROJECT_CREATE')")
    public ApiResponse<ProjectResponse> create(@Valid @RequestBody ProjectRequest request) {
        return ApiResponse.success(service.create(request), "/api/v1/projects");
    }

    @Operation(summary = "List", description = "Requires a valid JWT and any authority enforced by the endpoint.", responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Request completed successfully."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Request validation failed or a parameter is invalid."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Authentication is required or credentials are invalid."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "The authenticated user is not allowed to perform this operation."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "The requested resource was not found."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "The request conflicts with the current resource state.")
    })
    @GetMapping
    @PreAuthorize("hasAnyAuthority('PROJECT_READ','PROJECT_READ_TEAM','PROJECT_READ_SELF')")
    public ApiResponse<PageResponse<ProjectResponse>> list(
            @Parameter(description = "Request filter or option.") @RequestParam(required = false) String search,
            @Parameter(description = "Request filter or option.") @RequestParam(required = false) String status,
            @Parameter(description = "Request filter or option.") @RequestParam(required = false) UUID employeeId,
            @PageableDefault(size = 20, sort = "id") Pageable pageable
    ) {
        return ApiResponse.success(service.list(search, status, employeeId, pageable), "/api/v1/projects");
    }

    @Operation(summary = "Get", description = "Requires a valid JWT and any authority enforced by the endpoint.", responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Request completed successfully."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Request validation failed or a parameter is invalid."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Authentication is required or credentials are invalid."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "The authenticated user is not allowed to perform this operation."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "The requested resource was not found."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "The request conflicts with the current resource state.")
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('PROJECT_READ','PROJECT_READ_TEAM','PROJECT_READ_SELF')")
    public ApiResponse<ProjectResponse> get(@Parameter(description = "Resource identifier.") @PathVariable UUID id) {
        return ApiResponse.success(service.get(id), "/api/v1/projects/" + id);
    }

    @Operation(summary = "Update", description = "Requires a valid JWT and any authority enforced by the endpoint.", responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Request completed successfully."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Request validation failed or a parameter is invalid."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Authentication is required or credentials are invalid."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "The authenticated user is not allowed to perform this operation."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "The requested resource was not found."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "The request conflicts with the current resource state.")
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('PROJECT_UPDATE')")
    public ApiResponse<ProjectResponse> update(@Parameter(description = "Resource identifier.") @PathVariable UUID id, @Valid @RequestBody ProjectRequest request) {
        return ApiResponse.success(service.update(id, request), "/api/v1/projects/" + id);
    }

    @Operation(summary = "Status", description = "Requires a valid JWT and any authority enforced by the endpoint.", responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Request completed successfully."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Request validation failed or a parameter is invalid."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Authentication is required or credentials are invalid."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "The authenticated user is not allowed to perform this operation."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "The requested resource was not found."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "The request conflicts with the current resource state.")
    })
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAuthority('PROJECT_UPDATE')")
    public ApiResponse<ProjectResponse> status(@Parameter(description = "Resource identifier.") @PathVariable UUID id, @Valid @RequestBody ProjectStatusRequest request) {
        return ApiResponse.success(service.changeStatus(id, request), "/api/v1/projects/" + id + "/status");
    }

    @Operation(summary = "Members", description = "Requires a valid JWT and any authority enforced by the endpoint.", responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Request completed successfully."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Request validation failed or a parameter is invalid."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Authentication is required or credentials are invalid."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "The authenticated user is not allowed to perform this operation."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "The requested resource was not found."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "The request conflicts with the current resource state.")
    })
    @GetMapping("/{id}/members")
    @PreAuthorize("hasAnyAuthority('PROJECT_READ','PROJECT_READ_TEAM','PROJECT_READ_SELF')")
    public ApiResponse<List<ProjectMemberResponse>> members(@Parameter(description = "Resource identifier.") @PathVariable UUID id) {
        return ApiResponse.success(service.members(id), "/api/v1/projects/" + id + "/members");
    }

    @Operation(summary = "Add Member", description = "Requires a valid JWT and any authority enforced by the endpoint.", responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Request completed successfully."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Request validation failed or a parameter is invalid."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Authentication is required or credentials are invalid."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "The authenticated user is not allowed to perform this operation."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "The requested resource was not found."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "The request conflicts with the current resource state.")
    })
    @PostMapping("/{id}/members")
    @PreAuthorize("hasAuthority('PROJECT_UPDATE')")
    public ApiResponse<ProjectMemberResponse> addMember(
            @Parameter(description = "Resource identifier.") @PathVariable UUID id,
            @Valid @RequestBody ProjectMemberRequest request
    ) {
        return ApiResponse.success(service.addMember(id, request), "/api/v1/projects/" + id + "/members");
    }

    @Operation(summary = "Remove Member", description = "Requires a valid JWT and any authority enforced by the endpoint.", responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Request completed successfully."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Request validation failed or a parameter is invalid."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Authentication is required or credentials are invalid."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "The authenticated user is not allowed to perform this operation."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "The requested resource was not found."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "The request conflicts with the current resource state.")
    })
    @DeleteMapping("/{id}/members/{employeeId}")
    @PreAuthorize("hasAuthority('PROJECT_UPDATE')")
    public ApiResponse<Void> removeMember(@Parameter(description = "Resource identifier.") @PathVariable UUID id, @Parameter(description = "Resource identifier.") @PathVariable UUID employeeId) {
        service.removeMember(id, employeeId);
        return ApiResponse.success(null, "/api/v1/projects/" + id + "/members/" + employeeId);
    }
}
