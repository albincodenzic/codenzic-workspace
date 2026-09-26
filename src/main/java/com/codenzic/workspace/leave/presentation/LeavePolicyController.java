package com.codenzic.workspace.leave.presentation;

import com.codenzic.workspace.common.api.ApiResponse;
import com.codenzic.workspace.leave.application.LeaveService;
import com.codenzic.workspace.leave.presentation.dto.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class LeavePolicyController {
    private final LeaveService service;

    @GetMapping("/api/v1/leave-types")
    @PreAuthorize("hasAuthority('LEAVE_READ')")
    public ApiResponse<List<LeaveTypeResponse>> types() {
        return ApiResponse.success(service.types(), "/api/v1/leave-types");
    }

    @PostMapping("/api/v1/leave-types")
    @PreAuthorize("hasAuthority('LEAVE_TYPE_MANAGE')")
    public ApiResponse<LeaveTypeResponse> createType(@Valid @RequestBody LeaveTypeRequest request) {
        return ApiResponse.success(service.createType(request), "/api/v1/leave-types");
    }

    @PutMapping("/api/v1/leave-types/{id}")
    @PreAuthorize("hasAuthority('LEAVE_TYPE_MANAGE')")
    public ApiResponse<LeaveTypeResponse> updateType(@PathVariable UUID id, @Valid @RequestBody LeaveTypeRequest request) {
        return ApiResponse.success(service.updateType(id, request), "/api/v1/leave-types/" + id);
    }

    @GetMapping("/api/v1/leave-balances/me")
    @PreAuthorize("hasAuthority('LEAVE_VIEW_SELF')")
    public ApiResponse<List<LeaveBalanceResponse>> myBalances(@RequestParam @Min(2000) int year) {
        return ApiResponse.success(service.myBalances(year), "/api/v1/leave-balances/me");
    }

    @GetMapping("/api/v1/leave-balances/{employeeId}")
    @PreAuthorize("hasAuthority('LEAVE_BALANCE_MANAGE')")
    public ApiResponse<List<LeaveBalanceResponse>> balances(@PathVariable UUID employeeId, @RequestParam @Min(2000) int year) {
        return ApiResponse.success(service.balances(employeeId, year), "/api/v1/leave-balances/" + employeeId);
    }

    @PutMapping("/api/v1/leave-balances")
    @PreAuthorize("hasAuthority('LEAVE_BALANCE_MANAGE')")
    public ApiResponse<LeaveBalanceResponse> setBalance(@Valid @RequestBody LeaveBalanceRequest request) {
        return ApiResponse.success(service.setBalance(request), "/api/v1/leave-balances");
    }
}
