package com.codenzic.workspace.department.presentation;

import com.codenzic.workspace.common.api.ApiResponse;
import com.codenzic.workspace.department.application.DepartmentService;
import com.codenzic.workspace.department.presentation.dto.DepartmentRequest;
import com.codenzic.workspace.department.presentation.dto.DepartmentResponse;
import com.codenzic.workspace.department.presentation.dto.DepartmentStatusRequest;
import com.codenzic.workspace.employee.presentation.dto.EmployeeResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/departments")
@RequiredArgsConstructor
public class DepartmentController {
    private final DepartmentService service;

    @PostMapping
    @PreAuthorize("hasAuthority('DEPARTMENT_CREATE')")
    public ApiResponse<DepartmentResponse> create(@Valid @RequestBody DepartmentRequest request) {
        return ApiResponse.success(service.create(request), "/api/v1/departments");
    }

    @GetMapping
    @PreAuthorize("hasAuthority('DEPARTMENT_READ')")
    public ApiResponse<List<DepartmentResponse>> list() {
        return ApiResponse.success(service.list(), "/api/v1/departments");
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('DEPARTMENT_READ')")
    public ApiResponse<DepartmentResponse> get(@PathVariable UUID id) {
        return ApiResponse.success(service.get(id), "/api/v1/departments/" + id);
    }

    @GetMapping("/{id}/members")
    @PreAuthorize("hasAuthority('DEPARTMENT_READ')")
    public ApiResponse<List<EmployeeResponse>> members(@PathVariable UUID id) {
        return ApiResponse.success(service.members(id), "/api/v1/departments/" + id + "/members");
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('DEPARTMENT_UPDATE')")
    public ApiResponse<DepartmentResponse> update(@PathVariable UUID id, @Valid @RequestBody DepartmentRequest request) {
        return ApiResponse.success(service.update(id, request), "/api/v1/departments/" + id);
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAuthority('DEPARTMENT_UPDATE')")
    public ApiResponse<DepartmentResponse> status(
            @PathVariable UUID id,
            @Valid @RequestBody DepartmentStatusRequest request
    ) {
        return ApiResponse.success(service.changeStatus(id, request.status()), "/api/v1/departments/" + id + "/status");
    }
}
