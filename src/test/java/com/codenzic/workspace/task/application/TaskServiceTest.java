package com.codenzic.workspace.task.application;

import com.codenzic.workspace.audit.application.AuditLogService;
import com.codenzic.workspace.common.exception.BusinessException;
import com.codenzic.workspace.communication.application.NotificationService;
import com.codenzic.workspace.employee.infrastructure.EmployeeRepository;
import com.codenzic.workspace.identity.domain.AccountStatus;
import com.codenzic.workspace.identity.domain.User;
import com.codenzic.workspace.project.infrastructure.ProjectMemberRepository;
import com.codenzic.workspace.project.infrastructure.ProjectRepository;
import com.codenzic.workspace.task.domain.Task;
import com.codenzic.workspace.task.infrastructure.TaskCommentRepository;
import com.codenzic.workspace.task.infrastructure.TaskHistoryRepository;
import com.codenzic.workspace.task.infrastructure.TaskRepository;
import com.codenzic.workspace.task.presentation.dto.TaskAssignmentRequest;
import com.codenzic.workspace.task.presentation.dto.TaskStatusRequest;
import com.codenzic.workspace.team.infrastructure.TeamMemberRepository;
import com.codenzic.workspace.team.infrastructure.TeamRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class TaskServiceTest {
    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void taskCannotBeAssignedToEmployeeFromAnotherOrganization() {
        UUID organizationId = UUID.randomUUID();
        User user = new User("lead@example.test", "hash", "Team", "Lead",
                organizationId, AccountStatus.ACTIVE, false);
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(user, null,
                java.util.List.of(new SimpleGrantedAuthority("TASK_READ"))));
        UUID foreignEmployeeId = UUID.randomUUID();
        EmployeeRepository employees = mock(EmployeeRepository.class);
        when(employees.findByIdAndOrganizationId(foreignEmployeeId, organizationId)).thenReturn(Optional.empty());
        TaskRepository tasks = mock(TaskRepository.class);
        TaskService service = new TaskService(tasks, mock(TaskCommentRepository.class),
                mock(TaskHistoryRepository.class), mock(ProjectRepository.class), mock(TeamRepository.class),
                employees, mock(TeamMemberRepository.class), mock(AuditLogService.class),
                mock(NotificationService.class));

        BusinessException error = assertThrows(BusinessException.class,
                () -> service.assign(UUID.randomUUID(), new TaskAssignmentRequest(foreignEmployeeId)));

        assertEquals("EMPLOYEE_NOT_FOUND", error.getCode());
        verify(tasks, never()).findByIdAndOrganizationId(any(), any());
    }

    @Test
    void taskStatusChangePersistsTheNewStatusAndAuditHistory() {
        UUID organizationId = UUID.randomUUID();
        UUID taskId = UUID.randomUUID();
        User user = mock(User.class);
        when(user.getId()).thenReturn(UUID.randomUUID());
        when(user.getOrganizationId()).thenReturn(organizationId);
        doReturn(java.util.List.of(new SimpleGrantedAuthority("TASK_READ"))).when(user).getAuthorities();
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(user, null,
                java.util.List.of(new SimpleGrantedAuthority("TASK_READ"))));
        Task task = new Task(organizationId, UUID.randomUUID(), null, null, "Review API",
                null, "TODO", "MEDIUM", null);
        TaskRepository tasks = mock(TaskRepository.class);
        when(tasks.findByIdAndOrganizationId(taskId, organizationId)).thenReturn(Optional.of(task));
        when(tasks.save(task)).thenReturn(task);
        TaskHistoryRepository history = mock(TaskHistoryRepository.class);
        TaskService service = new TaskService(tasks, mock(TaskCommentRepository.class), history,
                mock(ProjectRepository.class), mock(TeamRepository.class), mock(EmployeeRepository.class),
                mock(TeamMemberRepository.class), mock(AuditLogService.class), mock(NotificationService.class));

        var response = service.changeStatus(taskId, new TaskStatusRequest("IN_PROGRESS"));

        assertEquals("IN_PROGRESS", response.status());
        verify(history).save(any());
        verify(tasks).save(task);
    }
}
