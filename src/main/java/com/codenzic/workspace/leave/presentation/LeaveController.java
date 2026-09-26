package com.codenzic.workspace.leave.presentation;

import com.codenzic.workspace.common.api.ApiResponse;
import com.codenzic.workspace.leave.application.LeaveService;
import com.codenzic.workspace.leave.presentation.dto.LeaveDecisionRequest;
import com.codenzic.workspace.leave.presentation.dto.LeaveRequestDto;
import com.codenzic.workspace.leave.presentation.dto.LeaveResponse;
import com.codenzic.workspace.leave.presentation.dto.LeaveReviewRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/leave-requests")
@RequiredArgsConstructor
public class LeaveController {
    private final LeaveService service;

    @PostMapping
    @PreAuthorize("hasAnyAuthority('LEAVE_CREATE','LEAVE_APPLY')")
    public ApiResponse<LeaveResponse> create(@Valid @RequestBody LeaveRequestDto request) {
        return ApiResponse.success(service.create(request), "/api/v1/leave-requests");
    }

    @GetMapping
    @PreAuthorize("hasAuthority('LEAVE_READ')")
    public ApiResponse<List<LeaveResponse>> list(@RequestParam(required = false) String status) {
        return ApiResponse.success(service.list(status), "/api/v1/leave-requests");
    }

    @GetMapping("/me")
    @PreAuthorize("hasAuthority('LEAVE_VIEW_SELF')")
    public ApiResponse<List<LeaveResponse>> myRequests() {
        return ApiResponse.success(service.myRequests(), "/api/v1/leave-requests/me");
    }

    @GetMapping("/calendar")
    @PreAuthorize("hasAuthority('LEAVE_READ')")
    public ApiResponse<List<LeaveResponse>> calendar(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to
    ) {
        return ApiResponse.success(service.calendar(from, to), "/api/v1/leave-requests/calendar");
    }

    @GetMapping("/monitor")
    @PreAuthorize("hasAuthority('LEAVE_MONITOR')")
    public ApiResponse<List<LeaveResponse>> monitor(@RequestParam(required = false) String status) {
        return ApiResponse.success(service.monitor(status), "/api/v1/leave-requests/monitor");
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('LEAVE_READ','LEAVE_VIEW_SELF')")
    public ApiResponse<LeaveResponse> get(@PathVariable UUID id) {
        return ApiResponse.success(service.get(id), "/api/v1/leave-requests/" + id);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('LEAVE_CREATE','LEAVE_APPLY')")
    public ApiResponse<LeaveResponse> update(@PathVariable UUID id, @Valid @RequestBody LeaveRequestDto request) {
        return ApiResponse.success(service.update(id, request), "/api/v1/leave-requests/" + id);
    }

    @PatchMapping("/{id}/approve")
    @PreAuthorize("hasAuthority('LEAVE_APPROVE')")
    public ApiResponse<LeaveResponse> approve(@PathVariable UUID id, @Valid @RequestBody LeaveDecisionRequest request) {
        return ApiResponse.success(service.review(id, new LeaveReviewRequest("APPROVED", request.comment())),
                "/api/v1/leave-requests/" + id + "/approve");
    }

    @PatchMapping("/{id}/reject")
    @PreAuthorize("hasAuthority('LEAVE_APPROVE')")
    public ApiResponse<LeaveResponse> reject(@PathVariable UUID id, @Valid @RequestBody LeaveDecisionRequest request) {
        return ApiResponse.success(service.review(id, new LeaveReviewRequest("REJECTED", request.comment())),
                "/api/v1/leave-requests/" + id + "/reject");
    }

    @PatchMapping("/{id}/cancel")
    @PreAuthorize("hasAnyAuthority('LEAVE_CREATE','LEAVE_APPLY','LEAVE_VIEW_SELF')")
    public ApiResponse<LeaveResponse> cancel(@PathVariable UUID id) {
        return ApiResponse.success(service.cancel(id), "/api/v1/leave-requests/" + id + "/cancel");
    }

    @PostMapping("/{id}/review")
    @PreAuthorize("hasAuthority('LEAVE_APPROVE')")
    public ApiResponse<LeaveResponse> review(@PathVariable UUID id, @Valid @RequestBody LeaveReviewRequest request) {
        return ApiResponse.success(service.review(id, request), "/api/v1/leave-requests/" + id + "/review");
    }
}
