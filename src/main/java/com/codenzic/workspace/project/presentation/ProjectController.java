package com.codenzic.workspace.project.presentation;

import com.codenzic.workspace.common.api.ApiResponse;
import com.codenzic.workspace.common.api.PageResponse;
import com.codenzic.workspace.project.application.ProjectService;
import com.codenzic.workspace.project.presentation.dto.ProjectMemberRequest;
import com.codenzic.workspace.project.presentation.dto.ProjectMemberResponse;
import com.codenzic.workspace.project.presentation.dto.ProjectRequest;
import com.codenzic.workspace.project.presentation.dto.ProjectResponse;
import com.codenzic.workspace.project.presentation.dto.ProjectStatusRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/projects")
@RequiredArgsConstructor
public class ProjectController {
    private final ProjectService service;

    @PostMapping
    @PreAuthorize("hasAuthority('PROJECT_CREATE')")
    public ApiResponse<ProjectResponse> create(@Valid @RequestBody ProjectRequest request) {
        return ApiResponse.success(service.create(request), "/api/v1/projects");
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('PROJECT_READ','PROJECT_READ_TEAM','PROJECT_READ_SELF')")
    public ApiResponse<PageResponse<ProjectResponse>> list(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) UUID employeeId,
            @PageableDefault(size = 20, sort = "id") Pageable pageable
    ) {
        return ApiResponse.success(service.list(search, status, employeeId, pageable), "/api/v1/projects");
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('PROJECT_READ','PROJECT_READ_TEAM','PROJECT_READ_SELF')")
    public ApiResponse<ProjectResponse> get(@PathVariable UUID id) {
        return ApiResponse.success(service.get(id), "/api/v1/projects/" + id);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('PROJECT_UPDATE')")
    public ApiResponse<ProjectResponse> update(@PathVariable UUID id, @Valid @RequestBody ProjectRequest request) {
        return ApiResponse.success(service.update(id, request), "/api/v1/projects/" + id);
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAuthority('PROJECT_UPDATE')")
    public ApiResponse<ProjectResponse> status(@PathVariable UUID id, @Valid @RequestBody ProjectStatusRequest request) {
        return ApiResponse.success(service.changeStatus(id, request), "/api/v1/projects/" + id + "/status");
    }

    @GetMapping("/{id}/members")
    @PreAuthorize("hasAnyAuthority('PROJECT_READ','PROJECT_READ_TEAM','PROJECT_READ_SELF')")
    public ApiResponse<List<ProjectMemberResponse>> members(@PathVariable UUID id) {
        return ApiResponse.success(service.members(id), "/api/v1/projects/" + id + "/members");
    }

    @PostMapping("/{id}/members")
    @PreAuthorize("hasAuthority('PROJECT_UPDATE')")
    public ApiResponse<ProjectMemberResponse> addMember(
            @PathVariable UUID id,
            @Valid @RequestBody ProjectMemberRequest request
    ) {
        return ApiResponse.success(service.addMember(id, request), "/api/v1/projects/" + id + "/members");
    }

    @DeleteMapping("/{id}/members/{employeeId}")
    @PreAuthorize("hasAuthority('PROJECT_UPDATE')")
    public ApiResponse<Void> removeMember(@PathVariable UUID id, @PathVariable UUID employeeId) {
        service.removeMember(id, employeeId);
        return ApiResponse.success(null, "/api/v1/projects/" + id + "/members/" + employeeId);
    }
}
