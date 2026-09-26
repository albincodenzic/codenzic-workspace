package com.codenzic.workspace.task.presentation;

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
public class TaskController {
    private final TaskService service;

    @PostMapping
    @PreAuthorize("hasAuthority('TASK_CREATE')")
    public ApiResponse<TaskResponse> create(@Valid @RequestBody TaskRequest request) {
        return ApiResponse.success(service.create(request), "/api/v1/tasks");
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('TASK_READ','TASK_READ_TEAM','TASK_READ_SELF')")
    public ApiResponse<PageResponse<TaskResponse>> list(
            @RequestParam(required = false) UUID projectId,
            @RequestParam(required = false) UUID employeeId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String priority,
            @RequestParam(required = false) String search,
            @PageableDefault(size = 20, sort = "id") Pageable pageable
    ) {
        return ApiResponse.success(service.list(projectId, employeeId, status, priority, search, pageable), "/api/v1/tasks");
    }

    @GetMapping("/me")
    @PreAuthorize("hasAnyAuthority('TASK_READ','TASK_READ_TEAM','TASK_READ_SELF')")
    public ApiResponse<PageResponse<TaskResponse>> myTasks(@PageableDefault(size = 20, sort = "id") Pageable pageable) {
        return ApiResponse.success(service.myTasks(pageable), "/api/v1/tasks/me");
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('TASK_READ','TASK_READ_TEAM','TASK_READ_SELF')")
    public ApiResponse<TaskResponse> get(@PathVariable UUID id) {
        return ApiResponse.success(service.get(id), "/api/v1/tasks/" + id);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('TASK_UPDATE','TASK_UPDATE_TEAM','TASK_UPDATE_SELF')")
    public ApiResponse<TaskResponse> update(@PathVariable UUID id, @Valid @RequestBody TaskRequest request) {
        return ApiResponse.success(service.update(id, request), "/api/v1/tasks/" + id);
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyAuthority('TASK_UPDATE','TASK_UPDATE_TEAM','TASK_UPDATE_SELF')")
    public ApiResponse<TaskResponse> status(@PathVariable UUID id, @Valid @RequestBody TaskStatusRequest request) {
        return ApiResponse.success(service.changeStatus(id, request), "/api/v1/tasks/" + id + "/status");
    }

    @PatchMapping("/{id}/priority")
    @PreAuthorize("hasAnyAuthority('TASK_UPDATE','TASK_UPDATE_TEAM','TASK_UPDATE_SELF')")
    public ApiResponse<TaskResponse> priority(@PathVariable UUID id, @Valid @RequestBody TaskPriorityRequest request) {
        return ApiResponse.success(service.changePriority(id, request), "/api/v1/tasks/" + id + "/priority");
    }

    @PostMapping("/{id}/assign")
    @PreAuthorize("hasAuthority('TASK_ASSIGN')")
    public ApiResponse<TaskResponse> assign(@PathVariable UUID id, @Valid @RequestBody TaskAssignmentRequest request) {
        return ApiResponse.success(service.assign(id, request), "/api/v1/tasks/" + id + "/assign");
    }

    @GetMapping("/{id}/comments")
    @PreAuthorize("hasAnyAuthority('TASK_READ','TASK_READ_TEAM','TASK_READ_SELF')")
    public ApiResponse<List<TaskCommentResponse>> comments(@PathVariable UUID id) {
        return ApiResponse.success(service.comments(id), "/api/v1/tasks/" + id + "/comments");
    }

    @PostMapping("/{id}/comments")
    @PreAuthorize("hasAuthority('TASK_COMMENT')")
    public ApiResponse<TaskCommentResponse> comment(@PathVariable UUID id, @Valid @RequestBody TaskCommentRequest request) {
        return ApiResponse.success(service.addComment(id, request), "/api/v1/tasks/" + id + "/comments");
    }

    @GetMapping("/{id}/history")
    @PreAuthorize("hasAnyAuthority('TASK_READ','TASK_READ_TEAM','TASK_READ_SELF')")
    public ApiResponse<List<TaskHistoryResponse>> history(@PathVariable UUID id) {
        return ApiResponse.success(service.history(id), "/api/v1/tasks/" + id + "/history");
    }
}
