package com.codenzic.workspace.dashboard.application;

import com.codenzic.workspace.attendance.infrastructure.AttendanceRepository;
import com.codenzic.workspace.audit.application.AuditLogService;
import com.codenzic.workspace.communication.infrastructure.NotificationRepository;
import com.codenzic.workspace.dashboard.infrastructure.DashboardRepository;
import com.codenzic.workspace.employee.infrastructure.EmployeeRepository;
import com.codenzic.workspace.identity.domain.AccountStatus;
import com.codenzic.workspace.identity.domain.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class DashboardServiceTest {
    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void organizationDashboardMapsDatabaseAggregatesFromCurrentOrganization() {
        UUID organizationId = UUID.randomUUID();
        User user = new User("tenant@example.test", "hash", "Tenant", "User",
                organizationId, AccountStatus.ACTIVE, false);
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities()));
        DashboardRepository dashboard = mock(DashboardRepository.class);
        AuditLogService audit = mock(AuditLogService.class);
        when(dashboard.totals(organizationId)).thenReturn(new Object[]{18L, 4L, 12L, 3L, 2L, 9L, 1L, 2L});
        when(dashboard.taskStatuses(organizationId)).thenReturn(List.<Object[]>of(new Object[]{"IN_PROGRESS", 7L}));
        when(dashboard.departmentAttendance(organizationId)).thenReturn(List.of());
        when(dashboard.weeklyAttendance(organizationId)).thenReturn(List.of());
        when(dashboard.projectProgress(organizationId)).thenReturn(List.of());
        when(dashboard.upcomingBirthdays(organizationId)).thenReturn(List.of());
        when(audit.list(5, organizationId)).thenReturn(List.of());

        var response = new DashboardService(dashboard, mock(EmployeeRepository.class),
                mock(AttendanceRepository.class), mock(NotificationRepository.class),
                audit).get();

        assertEquals(18L, response.employees());
        assertEquals(4L, response.activeProjects());
        assertEquals(9L, response.presentToday());
        assertEquals(1L, response.employeesOnLeave());
        assertFalse(response.platformDashboard());
        verify(dashboard).totals(organizationId);
    }
}
