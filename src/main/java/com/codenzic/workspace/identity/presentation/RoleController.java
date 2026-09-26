package com.codenzic.workspace.identity.presentation;

import com.codenzic.workspace.common.api.ApiResponse;
import com.codenzic.workspace.identity.application.RoleService;
import com.codenzic.workspace.identity.presentation.dto.RoleRequest;
import com.codenzic.workspace.identity.presentation.dto.RolePermissionRequest;
import com.codenzic.workspace.identity.presentation.dto.RoleResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/roles")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ROLE_MANAGE')")
public class RoleController {
    private final RoleService roleService;

    @GetMapping
    public ApiResponse<List<RoleResponse>> list() {
        return ApiResponse.success(roleService.list(), "/api/v1/roles");
    }

    @GetMapping("/{id}")
    public ApiResponse<RoleResponse> get(@PathVariable UUID id) {
        return ApiResponse.success(roleService.get(id), "/api/v1/roles/" + id);
    }

    @PostMapping
    public ApiResponse<RoleResponse> create(@Valid @RequestBody RoleRequest request) {
        return ApiResponse.success(roleService.create(request), "/api/v1/roles");
    }

    @PutMapping("/{id}")
    public ApiResponse<RoleResponse> update(@PathVariable UUID id, @Valid @RequestBody RoleRequest request) {
        return ApiResponse.success(roleService.update(id, request), "/api/v1/roles/" + id);
    }

    @PostMapping("/{roleId}/permissions")
    public ApiResponse<RoleResponse> assignPermissions(
            @PathVariable UUID roleId,
            @Valid @RequestBody RolePermissionRequest request
    ) {
        return ApiResponse.success(roleService.assignPermissions(roleId, request.permissionCodes()),
                "/api/v1/roles/" + roleId + "/permissions");
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable UUID id) {
        roleService.delete(id);
        return ApiResponse.success(null, "/api/v1/roles/" + id);
    }

    @PostMapping("/{roleId}/users/{userId}")
    public ApiResponse<Void> assign(@PathVariable UUID roleId, @PathVariable UUID userId) {
        roleService.assign(roleId, userId);
        return ApiResponse.success(null, "/api/v1/roles/" + roleId + "/users/" + userId);
    }

    @DeleteMapping("/{roleId}/users/{userId}")
    public ApiResponse<Void> unassign(@PathVariable UUID roleId, @PathVariable UUID userId) {
        roleService.unassign(roleId, userId);
        return ApiResponse.success(null, "/api/v1/roles/" + roleId + "/users/" + userId);
    }
}
