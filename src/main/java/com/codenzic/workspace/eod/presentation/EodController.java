package com.codenzic.workspace.eod.presentation;

import com.codenzic.workspace.common.api.ApiResponse;
import com.codenzic.workspace.common.api.PageResponse;
import com.codenzic.workspace.eod.application.EodService;
import com.codenzic.workspace.eod.presentation.dto.EodRequest;
import com.codenzic.workspace.eod.presentation.dto.EodResponse;
import com.codenzic.workspace.eod.presentation.dto.EodReviewRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/eod-reports")
@RequiredArgsConstructor
public class EodController {
    private final EodService service;

    @PostMapping
    @PreAuthorize("hasAuthority('EOD_CREATE')")
    public ApiResponse<EodResponse> create(@Valid @RequestBody EodRequest request) {
        return ApiResponse.success(service.create(request), "/api/v1/eod-reports");
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('EOD_READ','EOD_READ_TEAM','EOD_READ_SELF')")
    public ApiResponse<PageResponse<EodResponse>> list(
            @RequestParam(required = false) UUID employeeId,
            @RequestParam(required = false) UUID teamId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(required = false) String status,
            @PageableDefault(size = 20, sort = "reportDate") Pageable pageable
    ) {
        return ApiResponse.success(service.list(employeeId, teamId, from, to, status, pageable), "/api/v1/eod-reports");
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('EOD_READ','EOD_READ_TEAM','EOD_READ_SELF')")
    public ApiResponse<EodResponse> get(@PathVariable UUID id) {
        return ApiResponse.success(service.get(id), "/api/v1/eod-reports/" + id);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('EOD_CREATE')")
    public ApiResponse<EodResponse> update(@PathVariable UUID id, @Valid @RequestBody EodRequest request) {
        return ApiResponse.success(service.update(id, request), "/api/v1/eod-reports/" + id);
    }

    @PatchMapping("/{id}/submit")
    @PreAuthorize("hasAuthority('EOD_CREATE')")
    public ApiResponse<EodResponse> submit(@PathVariable UUID id) {
        return ApiResponse.success(service.submit(id), "/api/v1/eod-reports/" + id + "/submit");
    }

    @PatchMapping("/{id}/review")
    @PreAuthorize("hasAnyAuthority('EOD_REVIEW','EOD_REVIEW_TEAM')")
    public ApiResponse<EodResponse> review(@PathVariable UUID id, @Valid @RequestBody EodReviewRequest request) {
        return ApiResponse.success(service.review(id, request.comment()), "/api/v1/eod-reports/" + id + "/review");
    }

    @PatchMapping("/{id}/reject")
    @PreAuthorize("hasAnyAuthority('EOD_REVIEW','EOD_REVIEW_TEAM')")
    public ApiResponse<EodResponse> reject(@PathVariable UUID id, @Valid @RequestBody EodReviewRequest request) {
        return ApiResponse.success(service.reject(id, request.comment()), "/api/v1/eod-reports/" + id + "/reject");
    }
}
