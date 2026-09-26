package com.codenzic.workspace.organization.presentation;

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
public class OrganizationController {
    private final OrganizationService organizationService;
    private final RoleService roleService;

    @PostMapping
    @PreAuthorize("hasAuthority('ORGANIZATION_CREATE')")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<OrganizationResponse> create(@Valid @RequestBody OrganizationRequest request) {
        return ApiResponse.success(organizationService.create(request), "/api/v1/organizations");
    }

    @GetMapping
    @PreAuthorize("hasAuthority('ORGANIZATION_READ')")
    public ApiResponse<List<OrganizationResponse>> list() {
        return ApiResponse.success(organizationService.list(), "/api/v1/organizations");
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ORGANIZATION_READ')")
    public ApiResponse<OrganizationResponse> get(@PathVariable UUID id) {
        return ApiResponse.success(organizationService.get(id), "/api/v1/organizations/" + id);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ORGANIZATION_UPDATE')")
    public ApiResponse<OrganizationResponse> update(
            @PathVariable UUID id,
            @Valid @RequestBody OrganizationRequest request
    ) {
        return ApiResponse.success(organizationService.update(id, request), "/api/v1/organizations/" + id);
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAuthority('ORGANIZATION_UPDATE')")
    public ApiResponse<OrganizationResponse> updateStatus(
            @PathVariable UUID id,
            @Valid @RequestBody OrganizationStatusRequest request
    ) {
        return ApiResponse.success(organizationService.updateStatus(id, request.status()), "/api/v1/organizations/" + id + "/status");
    }

    @PostMapping("/{id}/admins")
    @PreAuthorize("hasAuthority('ROLE_MANAGE')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void assignAdmin(@PathVariable UUID id, @Valid @RequestBody OrganizationAdminRequest request) {
        roleService.assignOrganizationAdmin(id, request.userId());
    }

    @DeleteMapping("/{id}/admins/{userId}")
    @PreAuthorize("hasAuthority('ROLE_MANAGE')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeAdmin(@PathVariable UUID id, @PathVariable UUID userId) {
        roleService.unassignOrganizationAdmin(id, userId);
    }
}
