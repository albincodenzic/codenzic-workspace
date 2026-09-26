package com.codenzic.workspace.organization.application;

import com.codenzic.workspace.audit.application.AuditLogService;
import com.codenzic.workspace.common.exception.BusinessException;
import com.codenzic.workspace.identity.application.RoleService;
import com.codenzic.workspace.identity.domain.AccountStatus;
import com.codenzic.workspace.identity.domain.User;
import com.codenzic.workspace.organization.infrastructure.OrganizationRepository;
import com.codenzic.workspace.organization.presentation.dto.OrganizationRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

class OrganizationServiceTest {
    private final OrganizationRepository organizations = mock(OrganizationRepository.class);
    private final RoleService roles = mock(RoleService.class);
    private final OrganizationService service = new OrganizationService(organizations, roles, mock(AuditLogService.class));

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void organizationAdminCannotManageOrganizationMetadata() {
        UUID organizationId = UUID.randomUUID();
        User tenantUser = new User("admin@example.com", "hash", "Org", "Admin",
                organizationId, AccountStatus.ACTIVE, false);
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(tenantUser, null, tenantUser.getAuthorities()));

        BusinessException exception = assertThrows(BusinessException.class,
                () -> service.update(organizationId, new OrganizationRequest("Updated", "updated", null)));

        assertEquals("PLATFORM_SCOPE_REQUIRED", exception.getCode());
        assertEquals(403, exception.getStatus().value());
    }
}
