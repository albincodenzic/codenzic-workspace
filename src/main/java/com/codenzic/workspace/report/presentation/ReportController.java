package com.codenzic.workspace.report.presentation;

import com.codenzic.workspace.common.api.ApiResponse;
import com.codenzic.workspace.report.application.ReportService;
import com.codenzic.workspace.report.presentation.dto.ReportDtos.Attendance;
import com.codenzic.workspace.report.presentation.dto.ReportDtos.Eod;
import com.codenzic.workspace.report.presentation.dto.ReportDtos.Employee;
import com.codenzic.workspace.report.presentation.dto.ReportDtos.Leave;
import com.codenzic.workspace.report.presentation.dto.ReportDtos.Project;
import com.codenzic.workspace.report.presentation.dto.ReportDtos.Response;
import com.codenzic.workspace.report.presentation.dto.ReportDtos.Task;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/reports")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('REPORT_VIEW')")
public class ReportController {
    private final ReportService service;

    @GetMapping
    public ApiResponse<Response> get(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to
    ) {
        return ApiResponse.success(service.get(from, to), "/api/v1/reports");
    }

    @GetMapping("/attendance")
    public ApiResponse<List<Attendance>> attendance(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(required = false) UUID employeeId,
            @RequestParam(required = false) UUID departmentId,
            @RequestParam(required = false) UUID teamId,
            @RequestParam(required = false) String status
    ) {
        return ApiResponse.success(service.attendance(from, to, employeeId, departmentId, teamId, status),
                "/api/v1/reports/attendance");
    }

    @GetMapping("/leave")
    public ApiResponse<List<Leave>> leave(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(required = false) UUID employeeId,
            @RequestParam(required = false) UUID departmentId,
            @RequestParam(required = false) UUID teamId,
            @RequestParam(required = false) String status
    ) {
        return ApiResponse.success(service.leave(from, to, employeeId, departmentId, teamId, status),
                "/api/v1/reports/leave");
    }

    @GetMapping("/employees")
    public ApiResponse<List<Employee>> employees(
            @RequestParam(required = false) UUID employeeId,
            @RequestParam(required = false) UUID departmentId,
            @RequestParam(required = false) UUID teamId,
            @RequestParam(required = false) String status
    ) {
        return ApiResponse.success(service.employees(employeeId, departmentId, teamId, status),
                "/api/v1/reports/employees");
    }

    @GetMapping("/projects")
    public ApiResponse<List<Project>> projects(
            @RequestParam(required = false) UUID projectId,
            @RequestParam(required = false) UUID employeeId,
            @RequestParam(required = false) String status
    ) {
        return ApiResponse.success(service.projects(projectId, employeeId, status), "/api/v1/reports/projects");
    }

    @GetMapping("/tasks")
    public ApiResponse<List<Task>> tasks(
            @RequestParam(required = false) UUID projectId,
            @RequestParam(required = false) UUID employeeId,
            @RequestParam(required = false) UUID departmentId,
            @RequestParam(required = false) UUID teamId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String priority
    ) {
        return ApiResponse.success(service.tasks(projectId, employeeId, departmentId, teamId, status, priority),
                "/api/v1/reports/tasks");
    }

    @GetMapping("/eod")
    public ApiResponse<List<Eod>> eod(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(required = false) UUID employeeId,
            @RequestParam(required = false) UUID teamId,
            @RequestParam(required = false) String status
    ) {
        return ApiResponse.success(service.eod(from, to, employeeId, teamId, status), "/api/v1/reports/eod");
    }
}
