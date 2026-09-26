package com.codenzic.workspace.employee.application;

import com.codenzic.workspace.audit.application.AuditLogService;
import com.codenzic.workspace.common.exception.BusinessException;
import com.codenzic.workspace.employee.infrastructure.EmployeeRepository;
import com.codenzic.workspace.identity.domain.AccountStatus;
import com.codenzic.workspace.identity.domain.User;
import com.codenzic.workspace.identity.infrastructure.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class EmployeeServiceTest {
    private final EmployeeRepository employees = mock(EmployeeRepository.class);
    private final UserRepository users = mock(UserRepository.class);
    private final EmployeeService service = new EmployeeService(employees, users, mock(AuditLogService.class));

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void organizationUserCannotReadEmployeeFromAnotherOrganization() {
        UUID organizationA = UUID.randomUUID();
        UUID employeeInOrganizationB = UUID.randomUUID();
        User user = new User("member@example.com", "hash", "Org", "Member",
                organizationA, AccountStatus.ACTIVE, false);
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities()));
        when(employees.findByIdAndOrganizationId(employeeInOrganizationB, organizationA))
                .thenReturn(Optional.empty());

        BusinessException exception = assertThrows(BusinessException.class,
                () -> service.get(employeeInOrganizationB));

        assertEquals("EMPLOYEE_NOT_FOUND", exception.getCode());
        verify(employees).findByIdAndOrganizationId(employeeInOrganizationB, organizationA);
    }
}
