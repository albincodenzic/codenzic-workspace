package com.codenzic.workspace.attendance.presentation;

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
public class AttendanceController {
    private final AttendanceService service;

    @PostMapping
    @PreAuthorize("hasAuthority('ATTENDANCE_CREATE')")
    public ApiResponse<AttendanceResponse> create(@Valid @RequestBody AttendanceRequest request) {
        return ApiResponse.success(service.create(request), "/api/v1/attendance");
    }

    @GetMapping
    @PreAuthorize("hasAuthority('ATTENDANCE_READ')")
    public ApiResponse<List<AttendanceResponse>> list() {
        return ApiResponse.success(service.list(), "/api/v1/attendance");
    }

    @GetMapping("/me")
    @PreAuthorize("hasAuthority('ATTENDANCE_VIEW_SELF')")
    public ApiResponse<AttendanceResponse> today() {
        return ApiResponse.success(service.todayForMe(), "/api/v1/attendance/me");
    }

    @GetMapping("/me/history")
    @PreAuthorize("hasAuthority('ATTENDANCE_VIEW_SELF')")
    public ApiResponse<List<AttendanceResponse>> myHistory(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
                                                            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return ApiResponse.success(service.myHistory(from, to), "/api/v1/attendance/me/history");
    }

    @GetMapping("/me/calendar")
    @PreAuthorize("hasAuthority('ATTENDANCE_VIEW_SELF')")
    public ApiResponse<List<AttendanceResponse>> myCalendar(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to
    ) {
        return ApiResponse.success(service.myCalendar(from, to), "/api/v1/attendance/me/calendar");
    }

    @GetMapping("/calendar")
    @PreAuthorize("hasAuthority('ATTENDANCE_VIEW_ORGANIZATION')")
    public ApiResponse<List<AttendanceResponse>> calendar(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
                                                           @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return ApiResponse.success(service.calendar(from, to), "/api/v1/attendance/calendar");
    }

    @GetMapping("/monitor")
    @PreAuthorize("hasAuthority('ATTENDANCE_MONITOR')")
    public ApiResponse<PageResponse<AttendanceResponse>> monitor(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false) UUID employeeId,
            @RequestParam(required = false) UUID departmentId,
            @RequestParam(required = false) UUID teamId,
            @RequestParam(required = false) String status,
            @PageableDefault(size = 20, sort = "attendanceDate") Pageable pageable
    ) {
        return ApiResponse.success(service.monitor(date == null ? LocalDate.now() : date, employeeId,
                departmentId, teamId, status, pageable), "/api/v1/attendance/monitor");
    }

    @PostMapping("/check-in")
    @PreAuthorize("hasAuthority('ATTENDANCE_CHECK_IN')")
    public ApiResponse<AttendancePunchResponse> checkIn() {
        return ApiResponse.success(service.checkIn(), "/api/v1/attendance/check-in");
    }

    @PostMapping("/check-out")
    @PreAuthorize("hasAuthority('ATTENDANCE_CHECK_OUT')")
    public ApiResponse<AttendancePunchResponse> checkOut() {
        return ApiResponse.success(service.checkOut(), "/api/v1/attendance/check-out");
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ATTENDANCE_READ')")
    public ApiResponse<AttendanceResponse> get(@PathVariable UUID id) {
        return ApiResponse.success(service.get(id), "/api/v1/attendance/" + id);
    }
}
