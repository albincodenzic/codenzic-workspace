package com.codenzic.workspace.identity.presentation;

import com.codenzic.workspace.common.api.ApiResponse;
import com.codenzic.workspace.identity.application.PermissionService;
import com.codenzic.workspace.identity.presentation.dto.PermissionResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/permissions")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('PERMISSION_MANAGE')")
public class PermissionController {
    private final PermissionService permissionService;

    @GetMapping
    public ApiResponse<List<PermissionResponse>> list() {
        return ApiResponse.success(permissionService.list(), "/api/v1/permissions");
    }

    @GetMapping("/{code}")
    public ApiResponse<PermissionResponse> get(@PathVariable String code) {
        return ApiResponse.success(permissionService.get(code), "/api/v1/permissions/" + code);
    }
}
