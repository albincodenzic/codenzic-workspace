package com.codenzic.workspace.eod.application;

import com.codenzic.workspace.audit.application.AuditLogService;
import com.codenzic.workspace.communication.application.NotificationService;
import com.codenzic.workspace.eod.domain.EodReport;
import com.codenzic.workspace.eod.infrastructure.EodReportRepository;
import com.codenzic.workspace.employee.domain.Employee;
import com.codenzic.workspace.employee.infrastructure.EmployeeRepository;
import com.codenzic.workspace.identity.domain.AccountStatus;
import com.codenzic.workspace.identity.domain.User;
import com.codenzic.workspace.team.infrastructure.TeamMemberRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class EodServiceTest {
    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void ownerCanSubmitDraftAndGeneratesAnAuditEvent() {
        UUID organizationId = UUID.randomUUID();
        UUID employeeId = UUID.randomUUID();
        UUID reportId = UUID.randomUUID();
        User user = new User("employee@example.test", "hash", "Employee", "One",
                organizationId, AccountStatus.ACTIVE, false);
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities()));
        Employee employee = mock(Employee.class);
        when(employee.getId()).thenReturn(employeeId);
        when(employee.getUserId()).thenReturn(user.getId());
        EmployeeRepository employees = mock(EmployeeRepository.class);
        when(employees.findByUserIdAndOrganizationId(user.getId(), organizationId)).thenReturn(Optional.of(employee));
        EodReportRepository reports = mock(EodReportRepository.class);
        EodReport draft = new EodReport(organizationId, employeeId, LocalDate.now(), "Completed API",
                null);
        when(reports.findByIdAndOrganizationId(reportId, organizationId)).thenReturn(Optional.of(draft));
        when(reports.save(draft)).thenReturn(draft);
        AuditLogService audit = mock(AuditLogService.class);
        EodService service = new EodService(reports, employees, mock(TeamMemberRepository.class), audit,
                mock(NotificationService.class));

        var response = service.submit(reportId);

        assertEquals("SUBMITTED", response.status());
        verify(audit).record(organizationId, user.getId(), "SUBMIT", "EOD", draft.getId(), "{}");
    }
}
