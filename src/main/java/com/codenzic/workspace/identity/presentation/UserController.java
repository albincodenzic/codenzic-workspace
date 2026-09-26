package com.codenzic.workspace.identity.presentation;

import com.codenzic.workspace.common.api.ApiResponse;
import com.codenzic.workspace.identity.application.UserService;
import com.codenzic.workspace.identity.presentation.dto.UserResponse;
import com.codenzic.workspace.identity.presentation.dto.UserUpdateRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('USER_MANAGE')")
public class UserController {

    private final UserService userService;

    @GetMapping
    public ApiResponse<List<UserResponse>> list() {
        return ApiResponse.success(userService.list(), "/api/v1/users");
    }

    @GetMapping("/{id}")
    public ApiResponse<UserResponse> get(@PathVariable UUID id) {
        return ApiResponse.success(userService.get(id), "/api/v1/users/" + id);
    }

    @PutMapping("/{id}")
    public ApiResponse<UserResponse> update(
            @PathVariable UUID id,
            @Valid @RequestBody UserUpdateRequest request
    ) {
        return ApiResponse.success(userService.update(id, request), "/api/v1/users/" + id);
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable UUID id) {
        userService.delete(id);
        return ApiResponse.success(null, "/api/v1/users/" + id);
    }
}