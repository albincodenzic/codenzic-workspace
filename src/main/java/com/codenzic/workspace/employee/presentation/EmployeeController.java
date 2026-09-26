package com.codenzic.workspace.employee.presentation;

import com.codenzic.workspace.common.api.ApiResponse;
import com.codenzic.workspace.common.api.PageResponse;
import com.codenzic.workspace.employee.application.EmployeeService;
import com.codenzic.workspace.employee.domain.EmployeeStatus;
import com.codenzic.workspace.employee.presentation.dto.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/employees")
@RequiredArgsConstructor
public class EmployeeController {
    private final EmployeeService service;

    @PostMapping
    @PreAuthorize("hasAuthority('EMPLOYEE_CREATE')")
    public ApiResponse<EmployeeResponse> create(@Valid @RequestBody EmployeeRequest request) {
        return ApiResponse.success(service.create(request), "/api/v1/employees");
    }

    @GetMapping
    @PreAuthorize("hasAuthority('EMPLOYEE_READ')")
    public ApiResponse<PageResponse<EmployeeResponse>> list(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) EmployeeStatus status,
            @RequestParam(required = false) UUID departmentId,
            @RequestParam(required = false) UUID teamId,
            @RequestParam(required = false) String role,
            @PageableDefault(size = 20, sort = "id") Pageable pageable
    ) {
        return ApiResponse.success(service.list(search, status, departmentId, teamId, role, pageable), "/api/v1/employees");
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('EMPLOYEE_READ')")
    public ApiResponse<EmployeeResponse> get(@PathVariable UUID id) {
        return ApiResponse.success(service.get(id), "/api/v1/employees/" + id);
    }

    @PutMapping("/{id}")
    @PatchMapping("/{id}")
    @PreAuthorize("hasAuthority('EMPLOYEE_UPDATE')")
    public ApiResponse<EmployeeResponse> update(@PathVariable UUID id, @Valid @RequestBody EmployeeUpdateRequest request) {
        return ApiResponse.success(service.update(id, request), "/api/v1/employees/" + id);
    }

    @PostMapping("/{id}/deactivate")
    @PreAuthorize("hasAuthority('EMPLOYEE_DEACTIVATE')")
    public ApiResponse<EmployeeResponse> deactivate(@PathVariable UUID id) {
        return ApiResponse.success(service.deactivate(id), "/api/v1/employees/" + id + "/deactivate");
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAuthority('EMPLOYEE_UPDATE')")
    public ApiResponse<EmployeeResponse> status(
            @PathVariable UUID id,
            @Valid @RequestBody EmployeeStatusRequest request
    ) {
        return ApiResponse.success(service.changeStatus(id, request.status()), "/api/v1/employees/" + id + "/status");
    }
}
