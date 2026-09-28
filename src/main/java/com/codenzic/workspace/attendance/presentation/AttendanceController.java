package com.codenzic.workspace.attendance.presentation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import com.codenzic.workspace.attendance.application.AttendanceService;
import com.codenzic.workspace.attendance.presentation.dto.*;
import com.codenzic.workspace.common.api.ApiResponse;
import com.codenzic.workspace.common.api.PageResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/attendance")
@RequiredArgsConstructor
@Tag(name = "Attendance", description = "Attendance API operations.")
@SecurityRequirement(name = "bearerAuth")
public class AttendanceController {
    private final AttendanceService service;

    @Operation(summary = "Create", description = "Requires a valid JWT and any authority enforced by the endpoint.", responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Request completed successfully."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Request validation failed or a parameter is invalid."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Authentication is required or credentials are invalid."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "The authenticated user is not allowed to perform this operation."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "The requested resource was not found."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "The request conflicts with the current resource state.")
    })
    @PostMapping
    @PreAuthorize("hasAuthority('ATTENDANCE_CREATE')")
    public ApiResponse<AttendanceResponse> create(@Valid @RequestBody AttendanceRequest request) {
        return ApiResponse.success(service.create(request), "/api/v1/attendance");
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
    @PreAuthorize("hasAuthority('ATTENDANCE_READ')")
    public ApiResponse<List<AttendanceResponse>> list() {
        return ApiResponse.success(service.list(), "/api/v1/attendance");
    }

    @Operation(summary = "Today", description = "Requires a valid JWT and any authority enforced by the endpoint.", responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Request completed successfully."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Request validation failed or a parameter is invalid."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Authentication is required or credentials are invalid."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "The authenticated user is not allowed to perform this operation."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "The requested resource was not found."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "The request conflicts with the current resource state.")
    })
    @GetMapping("/me")
    @PreAuthorize("hasAuthority('ATTENDANCE_VIEW_SELF')")
    public ApiResponse<AttendanceResponse> today() {
        return ApiResponse.success(service.todayForMe(), "/api/v1/attendance/me");
    }

    @Operation(summary = "My History", description = "Requires a valid JWT and any authority enforced by the endpoint.", responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Request completed successfully."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Request validation failed or a parameter is invalid."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Authentication is required or credentials are invalid."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "The authenticated user is not allowed to perform this operation."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "The requested resource was not found."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "The request conflicts with the current resource state.")
    })
    @GetMapping("/me/history")
    @PreAuthorize("hasAuthority('ATTENDANCE_VIEW_SELF')")
    public ApiResponse<List<AttendanceResponse>> myHistory(@Parameter(description = "Request filter or option.") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
                                                            @Parameter(description = "Request filter or option.") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return ApiResponse.success(service.myHistory(from, to), "/api/v1/attendance/me/history");
    }

    @Operation(summary = "My Calendar", description = "Requires a valid JWT and any authority enforced by the endpoint.", responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Request completed successfully."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Request validation failed or a parameter is invalid."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Authentication is required or credentials are invalid."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "The authenticated user is not allowed to perform this operation."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "The requested resource was not found."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "The request conflicts with the current resource state.")
    })
    @GetMapping("/me/calendar")
    @PreAuthorize("hasAuthority('ATTENDANCE_VIEW_SELF')")
    public ApiResponse<List<AttendanceResponse>> myCalendar(
            @Parameter(description = "Request filter or option.") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @Parameter(description = "Request filter or option.") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to
    ) {
        return ApiResponse.success(service.myCalendar(from, to), "/api/v1/attendance/me/calendar");
    }

    @Operation(summary = "Calendar", description = "Requires a valid JWT and any authority enforced by the endpoint.", responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Request completed successfully."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Request validation failed or a parameter is invalid."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Authentication is required or credentials are invalid."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "The authenticated user is not allowed to perform this operation."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "The requested resource was not found."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "The request conflicts with the current resource state.")
    })
    @GetMapping("/calendar")
    @PreAuthorize("hasAuthority('ATTENDANCE_VIEW_ORGANIZATION')")
    public ApiResponse<List<AttendanceResponse>> calendar(@Parameter(description = "Request filter or option.") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
                                                           @Parameter(description = "Request filter or option.") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return ApiResponse.success(service.calendar(from, to), "/api/v1/attendance/calendar");
    }

    @Operation(summary = "Monitor", description = "Requires a valid JWT and any authority enforced by the endpoint.", responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Request completed successfully."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Request validation failed or a parameter is invalid."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Authentication is required or credentials are invalid."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "The authenticated user is not allowed to perform this operation."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "The requested resource was not found."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "The request conflicts with the current resource state.")
    })
    @GetMapping("/monitor")
    @PreAuthorize("hasAuthority('ATTENDANCE_MONITOR')")
    public ApiResponse<PageResponse<AttendanceResponse>> monitor(
            @Parameter(description = "Request filter or option.") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @Parameter(description = "Request filter or option.") @RequestParam(required = false) UUID employeeId,
            @Parameter(description = "Request filter or option.") @RequestParam(required = false) UUID departmentId,
            @Parameter(description = "Request filter or option.") @RequestParam(required = false) UUID teamId,
            @Parameter(description = "Request filter or option.") @RequestParam(required = false) String status,
            @PageableDefault(size = 20, sort = "attendanceDate") Pageable pageable
    ) {
        return ApiResponse.success(service.monitor(date == null ? LocalDate.now() : date, employeeId,
                departmentId, teamId, status, pageable), "/api/v1/attendance/monitor");
    }

    @Operation(summary = "Check In", description = "Requires a valid JWT and any authority enforced by the endpoint.", responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Request completed successfully."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Request validation failed or a parameter is invalid."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Authentication is required or credentials are invalid."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "The authenticated user is not allowed to perform this operation."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "The requested resource was not found."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "The request conflicts with the current resource state.")
    })
    @PostMapping("/check-in")
    @PreAuthorize("hasAuthority('ATTENDANCE_CHECK_IN')")
    public ApiResponse<AttendancePunchResponse> checkIn() {
        return ApiResponse.success(service.checkIn(), "/api/v1/attendance/check-in");
    }

    @Operation(summary = "Check Out", description = "Requires a valid JWT and any authority enforced by the endpoint.", responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Request completed successfully."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Request validation failed or a parameter is invalid."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Authentication is required or credentials are invalid."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "The authenticated user is not allowed to perform this operation."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "The requested resource was not found."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "The request conflicts with the current resource state.")
    })
    @PostMapping("/check-out")
    @PreAuthorize("hasAuthority('ATTENDANCE_CHECK_OUT')")
    public ApiResponse<AttendancePunchResponse> checkOut() {
        return ApiResponse.success(service.checkOut(), "/api/v1/attendance/check-out");
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
    @PreAuthorize("hasAuthority('ATTENDANCE_READ')")
    public ApiResponse<AttendanceResponse> get(@Parameter(description = "Resource identifier.") @PathVariable UUID id) {
        return ApiResponse.success(service.get(id), "/api/v1/attendance/" + id);
    }
}
