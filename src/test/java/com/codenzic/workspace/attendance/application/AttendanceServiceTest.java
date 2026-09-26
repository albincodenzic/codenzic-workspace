package com.codenzic.workspace.attendance.application;

import com.codenzic.workspace.audit.application.AuditLogService;
import com.codenzic.workspace.attendance.domain.Attendance;
import com.codenzic.workspace.attendance.infrastructure.AttendanceRepository;
import com.codenzic.workspace.common.exception.BusinessException;
import com.codenzic.workspace.employee.domain.Employee;
import com.codenzic.workspace.employee.infrastructure.EmployeeRepository;
import com.codenzic.workspace.identity.domain.AccountStatus;
import com.codenzic.workspace.identity.domain.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AttendanceServiceTest {
    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void duplicateCheckInIsRejectedWithoutWritingAnotherPunch() {
        UUID organizationId = UUID.randomUUID();
        UUID employeeId = UUID.randomUUID();
        User user = new User("worker@example.test", "hash", "Worker", "One",
                organizationId, AccountStatus.ACTIVE, false);
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities()));
        Employee employee = mock(Employee.class);
        when(employee.getId()).thenReturn(employeeId);
        EmployeeRepository employees = mock(EmployeeRepository.class);
        when(employees.findByUserIdAndOrganizationId(user.getId(), organizationId)).thenReturn(Optional.of(employee));
        AttendanceRepository attendance = mock(AttendanceRepository.class);
        when(attendance.findByEmployeeIdAndAttendanceDate(employeeId, LocalDate.now()))
                .thenReturn(Optional.of(new Attendance(organizationId, employeeId, LocalDate.now(),
                        "PRESENT", Instant.now(), null, null)));
        AttendanceService service = new AttendanceService(attendance, employees, mock(AuditLogService.class));

        BusinessException error = assertThrows(BusinessException.class, service::checkIn);

        assertEquals("INVALID_ATTENDANCE_TRANSITION", error.getCode());
        verify(attendance, never()).saveAndFlush(org.mockito.ArgumentMatchers.any());
    }

    @Test
    void checkOutWithoutCheckInIsRejected() {
        UUID organizationId = UUID.randomUUID();
        UUID employeeId = UUID.randomUUID();
        User user = new User("worker@example.test", "hash", "Worker", "One",
                organizationId, AccountStatus.ACTIVE, false);
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities()));
        Employee employee = mock(Employee.class);
        when(employee.getId()).thenReturn(employeeId);
        EmployeeRepository employees = mock(EmployeeRepository.class);
        when(employees.findByUserIdAndOrganizationId(user.getId(), organizationId)).thenReturn(Optional.of(employee));
        AttendanceRepository attendance = mock(AttendanceRepository.class);
        when(attendance.findByEmployeeIdAndAttendanceDate(employeeId, LocalDate.now())).thenReturn(Optional.empty());

        BusinessException error = assertThrows(BusinessException.class,
                () -> new AttendanceService(attendance, employees, mock(AuditLogService.class)).checkOut());

        assertEquals("ATTENDANCE_NOT_FOUND", error.getCode());
        verify(attendance, never()).save(org.mockito.ArgumentMatchers.any());
    }
}
