package com.codenzic.workspace.identity.application;

import com.codenzic.workspace.common.security.CurrentOrganizationContext;
import com.codenzic.workspace.common.security.CurrentUser;
import com.codenzic.workspace.identity.domain.AccountStatus;
import com.codenzic.workspace.identity.domain.User;
import com.codenzic.workspace.organization.domain.OrganizationStatus;
import com.codenzic.workspace.organization.infrastructure.OrganizationRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class OrganizationContextFilterTest {
    @AfterEach
    void clearContext() {
        SecurityContextHolder.clearContext();
        CurrentOrganizationContext.clear();
    }

    @Test
    void tenantCannotSelectAnotherOrganization() throws Exception {
        UUID ownOrganizationId = UUID.randomUUID();
        User tenant = new User("tenant@example.test", "hash", "Tenant", "User",
                ownOrganizationId, AccountStatus.ACTIVE, false);
        setAuthentication(tenant);
        OrganizationContextFilter filter = newFilter(mock(OrganizationRepository.class));
        MockHttpServletRequest request = request(UUID.randomUUID().toString());
        MockHttpServletResponse response = new MockHttpServletResponse();
        AtomicBoolean continued = new AtomicBoolean();

        filter.doFilter(request, response, (servletRequest, servletResponse) -> continued.set(true));

        assertEquals(403, response.getStatus());
        assertFalse(continued.get());
    }

    @Test
    void platformSelectionMustBeActiveAndIsClearedAfterRequest() throws Exception {
        UUID selectedOrganizationId = UUID.randomUUID();
        User platform = new User("admin@example.test", "hash", "Platform", "Admin",
                UUID.randomUUID(), AccountStatus.ACTIVE, true);
        setAuthentication(platform);
        OrganizationRepository organizations = mock(OrganizationRepository.class);
        when(organizations.existsByIdAndStatus(selectedOrganizationId, OrganizationStatus.ACTIVE)).thenReturn(true);
        OrganizationContextFilter filter = newFilter(organizations);
        MockHttpServletRequest request = request(selectedOrganizationId.toString());
        MockHttpServletResponse response = new MockHttpServletResponse();
        AtomicBoolean correctlyScoped = new AtomicBoolean();

        filter.doFilter(request, response, (servletRequest, servletResponse) ->
                correctlyScoped.set(selectedOrganizationId.equals(CurrentUser.requiredOrganizationId())));

        assertEquals(200, response.getStatus());
        assertTrue(correctlyScoped.get());
        assertNull(CurrentOrganizationContext.selectedOrganizationId());
    }

    private OrganizationContextFilter newFilter(OrganizationRepository organizations) {
        return new OrganizationContextFilter(organizations, new ObjectMapper().registerModule(new JavaTimeModule()));
    }

    private MockHttpServletRequest request(String organizationId) {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/api/v1/employees");
        request.addHeader(OrganizationContextFilter.ORGANIZATION_HEADER, organizationId);
        return request;
    }

    private void setAuthentication(User user) {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities()));
    }
}
