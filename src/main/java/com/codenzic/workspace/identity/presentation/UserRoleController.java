package com.codenzic.workspace.identity.presentation;

import com.codenzic.workspace.common.api.ApiResponse;
import com.codenzic.workspace.identity.application.RoleService;
import com.codenzic.workspace.identity.presentation.dto.UserRoleRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ROLE_MANAGE')")
public class UserRoleController {
    private final RoleService roleService;

    @PostMapping("/{userId}/roles")
    public ApiResponse<Void> assign(@PathVariable UUID userId, @Valid @RequestBody UserRoleRequest request) {
        roleService.assign(request.roleId(), userId);
        return ApiResponse.success(null, "/api/v1/users/" + userId + "/roles");
    }

    @DeleteMapping("/{userId}/roles/{roleId}")
    public ApiResponse<Void> unassign(@PathVariable UUID userId, @PathVariable UUID roleId) {
        roleService.unassign(roleId, userId);
        return ApiResponse.success(null, "/api/v1/users/" + userId + "/roles/" + roleId);
    }
}
