package com.codenzic.workspace.report.presentation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

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
@Tag(name = "Reports", description = "Reports API operations.")
@SecurityRequirement(name = "bearerAuth")
public class ReportController {
    private final ReportService service;

    @Operation(summary = "Get", description = "Requires a valid JWT and any authority enforced by the endpoint.", responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Request completed successfully."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Request validation failed or a parameter is invalid."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Authentication is required or credentials are invalid."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "The authenticated user is not allowed to perform this operation."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "The requested resource was not found."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "The request conflicts with the current resource state.")
    })
    @GetMapping
    public ApiResponse<Response> get(
            @Parameter(description = "Request filter or option.") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @Parameter(description = "Request filter or option.") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to
    ) {
        return ApiResponse.success(service.get(from, to), "/api/v1/reports");
    }

    @Operation(summary = "Attendance", description = "Requires a valid JWT and any authority enforced by the endpoint.", responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Request completed successfully."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Request validation failed or a parameter is invalid."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Authentication is required or credentials are invalid."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "The authenticated user is not allowed to perform this operation."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "The requested resource was not found."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "The request conflicts with the current resource state.")
    })
    @GetMapping("/attendance")
    public ApiResponse<List<Attendance>> attendance(
            @Parameter(description = "Request filter or option.") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @Parameter(description = "Request filter or option.") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @Parameter(description = "Request filter or option.") @RequestParam(required = false) UUID employeeId,
            @Parameter(description = "Request filter or option.") @RequestParam(required = false) UUID departmentId,
            @Parameter(description = "Request filter or option.") @RequestParam(required = false) UUID teamId,
            @Parameter(description = "Request filter or option.") @RequestParam(required = false) String status
    ) {
        return ApiResponse.success(service.attendance(from, to, employeeId, departmentId, teamId, status),
                "/api/v1/reports/attendance");
    }

    @Operation(summary = "Leave", description = "Requires a valid JWT and any authority enforced by the endpoint.", responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Request completed successfully."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Request validation failed or a parameter is invalid."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Authentication is required or credentials are invalid."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "The authenticated user is not allowed to perform this operation."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "The requested resource was not found."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "The request conflicts with the current resource state.")
    })
    @GetMapping("/leave")
    public ApiResponse<List<Leave>> leave(
            @Parameter(description = "Request filter or option.") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @Parameter(description = "Request filter or option.") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @Parameter(description = "Request filter or option.") @RequestParam(required = false) UUID employeeId,
            @Parameter(description = "Request filter or option.") @RequestParam(required = false) UUID departmentId,
            @Parameter(description = "Request filter or option.") @RequestParam(required = false) UUID teamId,
            @Parameter(description = "Request filter or option.") @RequestParam(required = false) String status
    ) {
        return ApiResponse.success(service.leave(from, to, employeeId, departmentId, teamId, status),
                "/api/v1/reports/leave");
    }

    @Operation(summary = "Employees", description = "Requires a valid JWT and any authority enforced by the endpoint.", responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Request completed successfully."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Request validation failed or a parameter is invalid."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Authentication is required or credentials are invalid."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "The authenticated user is not allowed to perform this operation."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "The requested resource was not found."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "The request conflicts with the current resource state.")
    })
    @GetMapping("/employees")
    public ApiResponse<List<Employee>> employees(
            @Parameter(description = "Request filter or option.") @RequestParam(required = false) UUID employeeId,
            @Parameter(description = "Request filter or option.") @RequestParam(required = false) UUID departmentId,
            @Parameter(description = "Request filter or option.") @RequestParam(required = false) UUID teamId,
            @Parameter(description = "Request filter or option.") @RequestParam(required = false) String status
    ) {
        return ApiResponse.success(service.employees(employeeId, departmentId, teamId, status),
                "/api/v1/reports/employees");
    }

    @Operation(summary = "Projects", description = "Requires a valid JWT and any authority enforced by the endpoint.", responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Request completed successfully."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Request validation failed or a parameter is invalid."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Authentication is required or credentials are invalid."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "The authenticated user is not allowed to perform this operation."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "The requested resource was not found."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "The request conflicts with the current resource state.")
    })
    @GetMapping("/projects")
    public ApiResponse<List<Project>> projects(
            @Parameter(description = "Request filter or option.") @RequestParam(required = false) UUID projectId,
            @Parameter(description = "Request filter or option.") @RequestParam(required = false) UUID employeeId,
            @Parameter(description = "Request filter or option.") @RequestParam(required = false) String status
    ) {
        return ApiResponse.success(service.projects(projectId, employeeId, status), "/api/v1/reports/projects");
    }

    @Operation(summary = "Tasks", description = "Requires a valid JWT and any authority enforced by the endpoint.", responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Request completed successfully."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Request validation failed or a parameter is invalid."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Authentication is required or credentials are invalid."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "The authenticated user is not allowed to perform this operation."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "The requested resource was not found."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "The request conflicts with the current resource state.")
    })
    @GetMapping("/tasks")
    public ApiResponse<List<Task>> tasks(
            @Parameter(description = "Request filter or option.") @RequestParam(required = false) UUID projectId,
            @Parameter(description = "Request filter or option.") @RequestParam(required = false) UUID employeeId,
            @Parameter(description = "Request filter or option.") @RequestParam(required = false) UUID departmentId,
            @Parameter(description = "Request filter or option.") @RequestParam(required = false) UUID teamId,
            @Parameter(description = "Request filter or option.") @RequestParam(required = false) String status,
            @Parameter(description = "Request filter or option.") @RequestParam(required = false) String priority
    ) {
        return ApiResponse.success(service.tasks(projectId, employeeId, departmentId, teamId, status, priority),
                "/api/v1/reports/tasks");
    }

    @Operation(summary = "Eod", description = "Requires a valid JWT and any authority enforced by the endpoint.", responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Request completed successfully."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Request validation failed or a parameter is invalid."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Authentication is required or credentials are invalid."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "The authenticated user is not allowed to perform this operation."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "The requested resource was not found."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "The request conflicts with the current resource state.")
    })
    @GetMapping("/eod")
    public ApiResponse<List<Eod>> eod(
            @Parameter(description = "Request filter or option.") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @Parameter(description = "Request filter or option.") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @Parameter(description = "Request filter or option.") @RequestParam(required = false) UUID employeeId,
            @Parameter(description = "Request filter or option.") @RequestParam(required = false) UUID teamId,
            @Parameter(description = "Request filter or option.") @RequestParam(required = false) String status
    ) {
        return ApiResponse.success(service.eod(from, to, employeeId, teamId, status), "/api/v1/reports/eod");
    }
}
