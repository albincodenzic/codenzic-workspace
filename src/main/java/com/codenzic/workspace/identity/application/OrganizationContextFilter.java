package com.codenzic.workspace.identity.application;

import com.codenzic.workspace.common.api.ApiErrorResponse;
import com.codenzic.workspace.common.security.CurrentOrganizationContext;
import com.codenzic.workspace.identity.domain.User;
import com.codenzic.workspace.organization.domain.OrganizationStatus;
import com.codenzic.workspace.organization.infrastructure.OrganizationRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Instant;
import java.util.UUID;

@Component
public class OrganizationContextFilter extends OncePerRequestFilter {
    public static final String ORGANIZATION_HEADER = "X-Organization-Id";

    private final OrganizationRepository organizations;
    private final ObjectMapper objectMapper;

    public OrganizationContextFilter(OrganizationRepository organizations) {
        this.organizations = organizations;
        this.objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object principal = authentication == null ? null : authentication.getPrincipal();
        String requestedOrganization = request.getHeader(ORGANIZATION_HEADER);

        if (!(principal instanceof User user) || requestedOrganization == null || requestedOrganization.isBlank()) {
            filterChain.doFilter(request, response);
            return;
        }

        UUID organizationId;
        try {
            organizationId = UUID.fromString(requestedOrganization);
        } catch (IllegalArgumentException exception) {
            writeError(response, HttpStatus.BAD_REQUEST, "INVALID_ORGANIZATION_SCOPE",
                    "Organization scope must be a valid UUID", request.getRequestURI());
            return;
        }

        // Validate access based on user type
        if (!user.isPlatformUser()) {
            if (!organizationId.equals(user.getOrganizationId())) {
                writeError(response, HttpStatus.FORBIDDEN, "ORGANIZATION_ACCESS_DENIED",
                        "You do not have access to this organisation", request.getRequestURI());
                return;
            }
        } else {
            if (!organizations.existsByIdAndStatus(organizationId, OrganizationStatus.ACTIVE)) {
                writeError(response, HttpStatus.NOT_FOUND, "ORGANIZATION_NOT_FOUND",
                        "Active organization not found", request.getRequestURI());
                return;
            }
        }

        // Set ThreadLocal context for both regular and platform users
        CurrentOrganizationContext.set(organizationId);
        try {
            filterChain.doFilter(request, response);
        } finally {
            // Always clear context to avoid memory leaks across request threads
            CurrentOrganizationContext.clear();
        }
    }

    private void writeError(
            HttpServletResponse response,
            HttpStatus status,
            String code,
            String message,
            String path
    ) throws IOException {
        response.setStatus(status.value());
        response.setContentType("application/json");
        objectMapper.writeValue(response.getOutputStream(),
                new ApiErrorResponse(Instant.now(), status.value(), code, message, path));
    }
}