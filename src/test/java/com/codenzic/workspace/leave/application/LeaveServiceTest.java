package com.codenzic.workspace.leave.application;

import com.codenzic.workspace.audit.application.AuditLogService;
import com.codenzic.workspace.common.exception.BusinessException;
import com.codenzic.workspace.communication.application.NotificationService;
import com.codenzic.workspace.employee.domain.Employee;
import com.codenzic.workspace.employee.infrastructure.EmployeeRepository;
import com.codenzic.workspace.identity.domain.User;
import com.codenzic.workspace.leave.domain.LeaveRequest;
import com.codenzic.workspace.leave.infrastructure.LeaveBalanceRepository;
import com.codenzic.workspace.leave.infrastructure.LeaveRequestRepository;
import com.codenzic.workspace.leave.infrastructure.LeaveTypeRepository;
import com.codenzic.workspace.leave.presentation.dto.LeaveReviewRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class LeaveServiceTest {
    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void employeeCannotApproveTheirOwnLeaveRequest() {
        UUID organizationId = UUID.randomUUID();
        UUID employeeId = UUID.randomUUID();
        UUID requestId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        User user = mock(User.class);
        when(user.getId()).thenReturn(userId);
        when(user.getOrganizationId()).thenReturn(organizationId);
        org.mockito.Mockito.doReturn(java.util.List.of(new SimpleGrantedAuthority("LEAVE_APPROVE")))
                .when(user).getAuthorities();
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities()));
        Employee employee = mock(Employee.class);
        when(employee.getId()).thenReturn(employeeId);
        when(employee.getUserId()).thenReturn(userId);
        EmployeeRepository employees = mock(EmployeeRepository.class);
        when(employees.findByUserIdAndOrganizationId(userId, organizationId)).thenReturn(Optional.of(employee));
        when(employees.findAllByOrganizationId(organizationId)).thenReturn(java.util.List.of(employee));
        LeaveRequestRepository requests = mock(LeaveRequestRepository.class);
        LeaveRequest leave = new LeaveRequest(organizationId, employeeId, "ANNUAL",
                LocalDate.now().plusDays(1), LocalDate.now().plusDays(2), "Personal");
        when(requests.findByIdAndOrganizationId(requestId, organizationId)).thenReturn(Optional.of(leave));
        LeaveService service = new LeaveService(requests, mock(LeaveTypeRepository.class),
                mock(LeaveBalanceRepository.class), employees, mock(AuditLogService.class),
                mock(NotificationService.class));

        BusinessException error = assertThrows(BusinessException.class,
                () -> service.review(requestId, new LeaveReviewRequest("APPROVED", null)));

        assertEquals("SELF_APPROVAL_FORBIDDEN", error.getCode());
        verify(requests, never()).save(leave);
    }
}
