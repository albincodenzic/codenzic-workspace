package com.codenzic.workspace.eod.presentation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

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
@Tag(name = "EOD", description = "EOD API operations.")
@SecurityRequirement(name = "bearerAuth")
public class EodController {
    private final EodService service;

    @Operation(summary = "Create", description = "Requires a valid JWT and any authority enforced by the endpoint.", responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Request completed successfully."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Request validation failed or a parameter is invalid."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Authentication is required or credentials are invalid."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "The authenticated user is not allowed to perform this operation."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "The requested resource was not found."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "The request conflicts with the current resource state.")
    })
    @PostMapping
    @PreAuthorize("hasAuthority('EOD_CREATE')")
    public ApiResponse<EodResponse> create(@Valid @RequestBody EodRequest request) {
        return ApiResponse.success(service.create(request), "/api/v1/eod-reports");
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
    @PreAuthorize("hasAnyAuthority('EOD_READ','EOD_READ_TEAM','EOD_READ_SELF')")
    public ApiResponse<PageResponse<EodResponse>> list(
            @Parameter(description = "Request filter or option.") @RequestParam(required = false) UUID employeeId,
            @Parameter(description = "Request filter or option.") @RequestParam(required = false) UUID teamId,
            @Parameter(description = "Request filter or option.") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @Parameter(description = "Request filter or option.") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @Parameter(description = "Request filter or option.") @RequestParam(required = false) String status,
            @PageableDefault(size = 20, sort = "reportDate") Pageable pageable
    ) {
        return ApiResponse.success(service.list(employeeId, teamId, from, to, status, pageable), "/api/v1/eod-reports");
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
    @PreAuthorize("hasAnyAuthority('EOD_READ','EOD_READ_TEAM','EOD_READ_SELF')")
    public ApiResponse<EodResponse> get(@Parameter(description = "Resource identifier.") @PathVariable UUID id) {
        return ApiResponse.success(service.get(id), "/api/v1/eod-reports/" + id);
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
    @PreAuthorize("hasAuthority('EOD_CREATE')")
    public ApiResponse<EodResponse> update(@Parameter(description = "Resource identifier.") @PathVariable UUID id, @Valid @RequestBody EodRequest request) {
        return ApiResponse.success(service.update(id, request), "/api/v1/eod-reports/" + id);
    }

    @Operation(summary = "Submit", description = "Requires a valid JWT and any authority enforced by the endpoint.", responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Request completed successfully."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Request validation failed or a parameter is invalid."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Authentication is required or credentials are invalid."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "The authenticated user is not allowed to perform this operation."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "The requested resource was not found."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "The request conflicts with the current resource state.")
    })
    @PatchMapping("/{id}/submit")
    @PreAuthorize("hasAuthority('EOD_CREATE')")
    public ApiResponse<EodResponse> submit(@Parameter(description = "Resource identifier.") @PathVariable UUID id) {
        return ApiResponse.success(service.submit(id), "/api/v1/eod-reports/" + id + "/submit");
    }

    @Operation(summary = "Review", description = "Requires a valid JWT and any authority enforced by the endpoint.", responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Request completed successfully."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Request validation failed or a parameter is invalid."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Authentication is required or credentials are invalid."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "The authenticated user is not allowed to perform this operation."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "The requested resource was not found."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "The request conflicts with the current resource state.")
    })
    @PatchMapping("/{id}/review")
    @PreAuthorize("hasAnyAuthority('EOD_REVIEW','EOD_REVIEW_TEAM')")
    public ApiResponse<EodResponse> review(@Parameter(description = "Resource identifier.") @PathVariable UUID id, @Valid @RequestBody EodReviewRequest request) {
        return ApiResponse.success(service.review(id, request.comment()), "/api/v1/eod-reports/" + id + "/review");
    }

    @Operation(summary = "Reject", description = "Requires a valid JWT and any authority enforced by the endpoint.", responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Request completed successfully."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Request validation failed or a parameter is invalid."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Authentication is required or credentials are invalid."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "The authenticated user is not allowed to perform this operation."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "The requested resource was not found."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "The request conflicts with the current resource state.")
    })
    @PatchMapping("/{id}/reject")
    @PreAuthorize("hasAnyAuthority('EOD_REVIEW','EOD_REVIEW_TEAM')")
    public ApiResponse<EodResponse> reject(@Parameter(description = "Resource identifier.") @PathVariable UUID id, @Valid @RequestBody EodReviewRequest request) {
        return ApiResponse.success(service.reject(id, request.comment()), "/api/v1/eod-reports/" + id + "/reject");
    }
}
