package com.codenzic.workspace.task.presentation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import com.codenzic.workspace.common.api.ApiResponse;
import com.codenzic.workspace.common.api.PageResponse;
import com.codenzic.workspace.task.application.TaskService;
import com.codenzic.workspace.task.presentation.dto.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/tasks")
@RequiredArgsConstructor
@Tag(name = "Tasks", description = "Tasks API operations.")
@SecurityRequirement(name = "bearerAuth")
public class TaskController {
    private final TaskService service;

    @Operation(summary = "Create", description = "Requires a valid JWT and any authority enforced by the endpoint.", responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Request completed successfully."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Request validation failed or a parameter is invalid."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Authentication is required or credentials are invalid."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "The authenticated user is not allowed to perform this operation."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "The requested resource was not found."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "The request conflicts with the current resource state.")
    })
    @PostMapping
    @PreAuthorize("hasAuthority('TASK_CREATE')")
    public ApiResponse<TaskResponse> create(@Valid @RequestBody TaskRequest request) {
        return ApiResponse.success(service.create(request), "/api/v1/tasks");
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
    @PreAuthorize("hasAnyAuthority('TASK_READ','TASK_READ_TEAM','TASK_READ_SELF')")
    public ApiResponse<PageResponse<TaskResponse>> list(
            @Parameter(description = "Request filter or option.") @RequestParam(required = false) UUID projectId,
            @Parameter(description = "Request filter or option.") @RequestParam(required = false) UUID employeeId,
            @Parameter(description = "Request filter or option.") @RequestParam(required = false) String status,
            @Parameter(description = "Request filter or option.") @RequestParam(required = false) String priority,
            @Parameter(description = "Request filter or option.") @RequestParam(required = false) String search,
            @PageableDefault(size = 20, sort = "id") Pageable pageable
    ) {
        return ApiResponse.success(service.list(projectId, employeeId, status, priority, search, pageable), "/api/v1/tasks");
    }

    @Operation(summary = "My Tasks", description = "Requires a valid JWT and any authority enforced by the endpoint.", responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Request completed successfully."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Request validation failed or a parameter is invalid."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Authentication is required or credentials are invalid."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "The authenticated user is not allowed to perform this operation."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "The requested resource was not found."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "The request conflicts with the current resource state.")
    })
    @GetMapping("/me")
    @PreAuthorize("hasAnyAuthority('TASK_READ','TASK_READ_TEAM','TASK_READ_SELF')")
    public ApiResponse<PageResponse<TaskResponse>> myTasks(@PageableDefault(size = 20, sort = "id") Pageable pageable) {
        return ApiResponse.success(service.myTasks(pageable), "/api/v1/tasks/me");
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
    @PreAuthorize("hasAnyAuthority('TASK_READ','TASK_READ_TEAM','TASK_READ_SELF')")
    public ApiResponse<TaskResponse> get(@Parameter(description = "Resource identifier.") @PathVariable UUID id) {
        return ApiResponse.success(service.get(id), "/api/v1/tasks/" + id);
    }

    @Operation(summary = "Update", description = "Requires a valid JWT and any authority enforced by the endpoint.", responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Request completed successfully."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Request validation failed or a parameter is invalid."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Authentication is required or credentials are invalid."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "The authenticated user is not allowed to perform this operation."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "The requested resource was not found."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "The request conflicts with the current resource state.")
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('TASK_UPDATE','TASK_UPDATE_TEAM','TASK_UPDATE_SELF')")
    public ApiResponse<TaskResponse> update(@Parameter(description = "Resource identifier.") @PathVariable UUID id, @Valid @RequestBody TaskRequest request) {
        return ApiResponse.success(service.update(id, request), "/api/v1/tasks/" + id);
    }

    @Operation(summary = "Status", description = "Requires a valid JWT and any authority enforced by the endpoint.", responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Request completed successfully."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Request validation failed or a parameter is invalid."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Authentication is required or credentials are invalid."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "The authenticated user is not allowed to perform this operation."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "The requested resource was not found."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "The request conflicts with the current resource state.")
    })
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyAuthority('TASK_UPDATE','TASK_UPDATE_TEAM','TASK_UPDATE_SELF')")
    public ApiResponse<TaskResponse> status(@Parameter(description = "Resource identifier.") @PathVariable UUID id, @Valid @RequestBody TaskStatusRequest request) {
        return ApiResponse.success(service.changeStatus(id, request), "/api/v1/tasks/" + id + "/status");
    }

    @Operation(summary = "Priority", description = "Requires a valid JWT and any authority enforced by the endpoint.", responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Request completed successfully."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Request validation failed or a parameter is invalid."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Authentication is required or credentials are invalid."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "The authenticated user is not allowed to perform this operation."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "The requested resource was not found."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "The request conflicts with the current resource state.")
    })
    @PatchMapping("/{id}/priority")
    @PreAuthorize("hasAnyAuthority('TASK_UPDATE','TASK_UPDATE_TEAM','TASK_UPDATE_SELF')")
    public ApiResponse<TaskResponse> priority(@Parameter(description = "Resource identifier.") @PathVariable UUID id, @Valid @RequestBody TaskPriorityRequest request) {
        return ApiResponse.success(service.changePriority(id, request), "/api/v1/tasks/" + id + "/priority");
    }

    @Operation(summary = "Assign", description = "Requires a valid JWT and any authority enforced by the endpoint.", responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Request completed successfully."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Request validation failed or a parameter is invalid."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Authentication is required or credentials are invalid."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "The authenticated user is not allowed to perform this operation."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "The requested resource was not found."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "The request conflicts with the current resource state.")
    })
    @PostMapping("/{id}/assign")
    @PreAuthorize("hasAuthority('TASK_ASSIGN')")
    public ApiResponse<TaskResponse> assign(@Parameter(description = "Resource identifier.") @PathVariable UUID id, @Valid @RequestBody TaskAssignmentRequest request) {
        return ApiResponse.success(service.assign(id, request), "/api/v1/tasks/" + id + "/assign");
    }

    @Operation(summary = "Comments", description = "Requires a valid JWT and any authority enforced by the endpoint.", responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Request completed successfully."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Request validation failed or a parameter is invalid."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Authentication is required or credentials are invalid."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "The authenticated user is not allowed to perform this operation."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "The requested resource was not found."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "The request conflicts with the current resource state.")
    })
    @GetMapping("/{id}/comments")
    @PreAuthorize("hasAnyAuthority('TASK_READ','TASK_READ_TEAM','TASK_READ_SELF')")
    public ApiResponse<List<TaskCommentResponse>> comments(@Parameter(description = "Resource identifier.") @PathVariable UUID id) {
        return ApiResponse.success(service.comments(id), "/api/v1/tasks/" + id + "/comments");
    }

    @Operation(summary = "Comment", description = "Requires a valid JWT and any authority enforced by the endpoint.", responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Request completed successfully."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Request validation failed or a parameter is invalid."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Authentication is required or credentials are invalid."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "The authenticated user is not allowed to perform this operation."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "The requested resource was not found."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "The request conflicts with the current resource state.")
    })
    @PostMapping("/{id}/comments")
    @PreAuthorize("hasAuthority('TASK_COMMENT')")
    public ApiResponse<TaskCommentResponse> comment(@Parameter(description = "Resource identifier.") @PathVariable UUID id, @Valid @RequestBody TaskCommentRequest request) {
        return ApiResponse.success(service.addComment(id, request), "/api/v1/tasks/" + id + "/comments");
    }

    @Operation(summary = "History", description = "Requires a valid JWT and any authority enforced by the endpoint.", responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Request completed successfully."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Request validation failed or a parameter is invalid."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Authentication is required or credentials are invalid."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "The authenticated user is not allowed to perform this operation."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "The requested resource was not found."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "The request conflicts with the current resource state.")
    })
    @GetMapping("/{id}/history")
    @PreAuthorize("hasAnyAuthority('TASK_READ','TASK_READ_TEAM','TASK_READ_SELF')")
    public ApiResponse<List<TaskHistoryResponse>> history(@Parameter(description = "Resource identifier.") @PathVariable UUID id) {
        return ApiResponse.success(service.history(id), "/api/v1/tasks/" + id + "/history");
    }
}
