package com.codenzic.workspace.team.presentation;

import com.codenzic.workspace.common.api.ApiResponse;
import com.codenzic.workspace.team.application.TeamService;
import com.codenzic.workspace.team.presentation.dto.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/teams")
@RequiredArgsConstructor
public class TeamController {
    private final TeamService service;

    @PostMapping
    @PreAuthorize("hasAuthority('TEAM_CREATE')")
    public ApiResponse<TeamResponse> create(@Valid @RequestBody TeamRequest request) {
        return ApiResponse.success(service.create(request), "/api/v1/teams");
    }

    @GetMapping
    @PreAuthorize("hasAuthority('TEAM_READ')")
    public ApiResponse<List<TeamResponse>> list() {
        return ApiResponse.success(service.list(), "/api/v1/teams");
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('TEAM_READ')")
    public ApiResponse<TeamResponse> get(@PathVariable UUID id) {
        return ApiResponse.success(service.get(id), "/api/v1/teams/" + id);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('TEAM_UPDATE')")
    public ApiResponse<TeamResponse> update(@PathVariable UUID id, @Valid @RequestBody TeamRequest request) {
        return ApiResponse.success(service.update(id, request), "/api/v1/teams/" + id);
    }

    @GetMapping("/{id}/members")
    @PreAuthorize("hasAuthority('TEAM_READ')")
    public ApiResponse<List<TeamMemberResponse>> members(@PathVariable UUID id) {
        return ApiResponse.success(service.members(id), "/api/v1/teams/" + id + "/members");
    }

    @PostMapping("/{id}/members")
    @PreAuthorize("hasAuthority('TEAM_MEMBER_MANAGE')")
    public ApiResponse<TeamMemberResponse> addMember(@PathVariable UUID id, @Valid @RequestBody TeamMemberRequest request) {
        return ApiResponse.success(service.addMember(id, request), "/api/v1/teams/" + id + "/members");
    }

    @DeleteMapping("/{id}/members/{employeeId}")
    @PreAuthorize("hasAuthority('TEAM_MEMBER_MANAGE')")
    public ApiResponse<Void> removeMember(@PathVariable UUID id, @PathVariable UUID employeeId) {
        service.removeMember(id, employeeId);
        return ApiResponse.success(null, "/api/v1/teams/" + id + "/members/" + employeeId);
    }
}
