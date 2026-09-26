package com.codenzic.workspace.company.presentation;

import com.codenzic.workspace.common.api.ApiResponse;
import com.codenzic.workspace.company.application.CompanySettingsService;
import com.codenzic.workspace.company.presentation.dto.CompanySettingsDto;
import jakarta.validation.Valid;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping({"/api/v1/company/settings", "/api/v1/settings"})
@RequiredArgsConstructor
public class CompanySettingsController {
    private final CompanySettingsService service;

    @GetMapping
    @PreAuthorize("hasAuthority('COMPANY_SETTINGS_READ')")
    public ApiResponse<CompanySettingsDto> get(HttpServletRequest request) {
        return ApiResponse.success(service.get(), request.getRequestURI());
    }

    @PutMapping
    @PreAuthorize("hasAuthority('COMPANY_SETTINGS_UPDATE')")
    public ApiResponse<CompanySettingsDto> update(
            @Valid @RequestBody CompanySettingsDto.Request requestBody,
            HttpServletRequest request
    ) {
        return ApiResponse.success(service.update(requestBody), request.getRequestURI());
    }
}
