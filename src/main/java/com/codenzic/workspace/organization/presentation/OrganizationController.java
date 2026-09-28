package com.codenzic.workspace.organization.presentation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import com.codenzic.workspace.common.api.ApiResponse;
import com.codenzic.workspace.identity.application.RoleService;
import com.codenzic.workspace.organization.application.OrganizationService;
import com.codenzic.workspace.organization.presentation.dto.OrganizationAdminRequest;
import com.codenzic.workspace.organization.presentation.dto.OrganizationRequest;
import com.codenzic.workspace.organization.presentation.dto.OrganizationResponse;
import com.codenzic.workspace.organization.presentation.dto.OrganizationStatusRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/organizations")
@RequiredArgsConstructor
@Tag(name = "Organizations", description = "Organizations API operations.")
@SecurityRequirement(name = "bearerAuth")
public class OrganizationController {
    private final OrganizationService organizationService;
    private final RoleService roleService;

    @Operation(summary = "Create", description = "Requires a valid JWT and any authority enforced by the endpoint.", responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Request completed successfully."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Request validation failed or a parameter is invalid."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Authentication is required or credentials are invalid."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "The authenticated user is not allowed to perform this operation."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "The requested resource was not found."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "The request conflicts with the current resource state.")
    })
    @PostMapping
    @PreAuthorize("hasAuthority('ORGANIZATION_CREATE')")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<OrganizationResponse> create(@Valid @RequestBody OrganizationRequest request) {
        return ApiResponse.success(organizationService.create(request), "/api/v1/organizations");
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
    @PreAuthorize("hasAuthority('ORGANIZATION_READ')")
    public ApiResponse<List<OrganizationResponse>> list() {
        return ApiResponse.success(organizationService.list(), "/api/v1/organizations");
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
    @PreAuthorize("hasAuthority('ORGANIZATION_READ')")
    public ApiResponse<OrganizationResponse> get(@Parameter(description = "Resource identifier.") @PathVariable UUID id) {
        return ApiResponse.success(organizationService.get(id), "/api/v1/organizations/" + id);
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
    @PreAuthorize("hasAuthority('ORGANIZATION_UPDATE')")
    public ApiResponse<OrganizationResponse> update(
            @Parameter(description = "Resource identifier.") @PathVariable UUID id,
            @Valid @RequestBody OrganizationRequest request
    ) {
        return ApiResponse.success(organizationService.update(id, request), "/api/v1/organizations/" + id);
    }

    @Operation(summary = "Update Status", description = "Requires a valid JWT and any authority enforced by the endpoint.", responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Request completed successfully."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Request validation failed or a parameter is invalid."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Authentication is required or credentials are invalid."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "The authenticated user is not allowed to perform this operation."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "The requested resource was not found."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "The request conflicts with the current resource state.")
    })
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAuthority('ORGANIZATION_UPDATE')")
    public ApiResponse<OrganizationResponse> updateStatus(
            @Parameter(description = "Resource identifier.") @PathVariable UUID id,
            @Valid @RequestBody OrganizationStatusRequest request
    ) {
        return ApiResponse.success(organizationService.updateStatus(id, request.status()), "/api/v1/organizations/" + id + "/status");
    }

    @Operation(summary = "Assign Admin", description = "Requires a valid JWT and any authority enforced by the endpoint.", responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "204", description = "Request completed successfully."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Request validation failed or a parameter is invalid."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Authentication is required or credentials are invalid."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "The authenticated user is not allowed to perform this operation."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "The requested resource was not found."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "The request conflicts with the current resource state.")
    })
    @PostMapping("/{id}/admins")
    @PreAuthorize("hasAuthority('ROLE_MANAGE')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void assignAdmin(@Parameter(description = "Resource identifier.") @PathVariable UUID id, @Valid @RequestBody OrganizationAdminRequest request) {
        roleService.assignOrganizationAdmin(id, request.userId());
    }

    @Operation(summary = "Remove Admin", description = "Requires a valid JWT and any authority enforced by the endpoint.", responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "204", description = "Request completed successfully."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Request validation failed or a parameter is invalid."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Authentication is required or credentials are invalid."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "The authenticated user is not allowed to perform this operation."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "The requested resource was not found."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "The request conflicts with the current resource state.")
    })
    @DeleteMapping("/{id}/admins/{userId}")
    @PreAuthorize("hasAuthority('ROLE_MANAGE')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeAdmin(@Parameter(description = "Resource identifier.") @PathVariable UUID id, @Parameter(description = "Resource identifier.") @PathVariable UUID userId) {
        roleService.unassignOrganizationAdmin(id, userId);
    }
}
