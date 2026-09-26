package com.codenzic.workspace.common.security;

import com.codenzic.workspace.audit.application.AuditLogService;
import com.codenzic.workspace.common.exception.BusinessException;
import com.codenzic.workspace.attendance.application.AttendanceService;
import com.codenzic.workspace.attendance.infrastructure.AttendanceRepository;
import com.codenzic.workspace.communication.application.AnnouncementService;
import com.codenzic.workspace.communication.application.ConversationService;
import com.codenzic.workspace.communication.application.NotificationService;
import com.codenzic.workspace.communication.domain.Announcement;
import com.codenzic.workspace.communication.domain.Conversation;
import com.codenzic.workspace.communication.infrastructure.AnnouncementRepository;
import com.codenzic.workspace.communication.infrastructure.ConversationParticipantRepository;
import com.codenzic.workspace.communication.infrastructure.ConversationRepository;
import com.codenzic.workspace.communication.infrastructure.MessageRepository;
import com.codenzic.workspace.communication.infrastructure.NotificationRepository;
import com.codenzic.workspace.company.application.CompanySettingsService;
import com.codenzic.workspace.company.infrastructure.CompanySettingsRepository;
import com.codenzic.workspace.department.application.DepartmentService;
import com.codenzic.workspace.department.infrastructure.DepartmentRepository;
import com.codenzic.workspace.eod.application.EodService;
import com.codenzic.workspace.eod.infrastructure.EodReportRepository;
import com.codenzic.workspace.employee.infrastructure.EmployeeRepository;
import com.codenzic.workspace.identity.domain.AccountStatus;
import com.codenzic.workspace.identity.domain.User;
import com.codenzic.workspace.identity.infrastructure.UserRepository;
import com.codenzic.workspace.leave.application.LeaveService;
import com.codenzic.workspace.leave.infrastructure.LeaveBalanceRepository;
import com.codenzic.workspace.leave.infrastructure.LeaveRequestRepository;
import com.codenzic.workspace.leave.infrastructure.LeaveTypeRepository;
import com.codenzic.workspace.project.application.ProjectService;
import com.codenzic.workspace.project.infrastructure.ProjectMemberRepository;
import com.codenzic.workspace.project.infrastructure.ProjectRepository;
import com.codenzic.workspace.report.application.ReportService;
import com.codenzic.workspace.report.infrastructure.ReportRepository;
import com.codenzic.workspace.task.application.TaskService;
import com.codenzic.workspace.task.infrastructure.TaskCommentRepository;
import com.codenzic.workspace.task.infrastructure.TaskHistoryRepository;
import com.codenzic.workspace.task.infrastructure.TaskRepository;
import com.codenzic.workspace.team.application.TeamService;
import com.codenzic.workspace.team.infrastructure.TeamMemberRepository;
import com.codenzic.workspace.team.infrastructure.TeamRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class OrganizationIsolationTest {
    private final UUID organizationA = UUID.randomUUID();
    private final UUID resourceInOrganizationB = UUID.randomUUID();
    private final User tenantUser = new User("a@example.test", "hash", "Org", "A",
            organizationA, AccountStatus.ACTIVE, false);

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void departmentLookupIsOrganizationScoped() {
        setCurrentUser();
        DepartmentRepository repository = mock(DepartmentRepository.class);
        when(repository.findByIdAndOrganizationId(resourceInOrganizationB, organizationA)).thenReturn(Optional.empty());

        assertThrows(BusinessException.class,
                () -> new DepartmentService(repository, mock(TeamRepository.class),
                        mock(TeamMemberRepository.class), mock(EmployeeRepository.class)).get(resourceInOrganizationB));

        verify(repository).findByIdAndOrganizationId(resourceInOrganizationB, organizationA);
    }

    @Test
    void teamLookupIsOrganizationScoped() {
        setCurrentUser();
        TeamRepository repository = mock(TeamRepository.class);
        when(repository.findByIdAndOrganizationId(resourceInOrganizationB, organizationA)).thenReturn(Optional.empty());

        assertThrows(BusinessException.class,
                () -> new TeamService(repository, mock(TeamMemberRepository.class),
                        mock(DepartmentRepository.class), mock(EmployeeRepository.class)).get(resourceInOrganizationB));

        verify(repository).findByIdAndOrganizationId(resourceInOrganizationB, organizationA);
    }

    @Test
    void projectLookupIsOrganizationScoped() {
        setCurrentUser();
        ProjectRepository repository = mock(ProjectRepository.class);
        when(repository.findByIdAndOrganizationId(resourceInOrganizationB, organizationA)).thenReturn(Optional.empty());

        assertThrows(BusinessException.class, () -> new ProjectService(repository,
                mock(ProjectMemberRepository.class), mock(EmployeeRepository.class), mock(AuditLogService.class))
                .get(resourceInOrganizationB));

        verify(repository).findByIdAndOrganizationId(resourceInOrganizationB, organizationA);
    }

    @Test
    void taskLookupIsOrganizationScoped() {
        setCurrentUser();
        TaskRepository repository = mock(TaskRepository.class);
        when(repository.findByIdAndOrganizationId(resourceInOrganizationB, organizationA)).thenReturn(Optional.empty());

        assertThrows(BusinessException.class, () -> new TaskService(repository, mock(TaskCommentRepository.class),
                mock(TaskHistoryRepository.class), mock(ProjectRepository.class), mock(TeamRepository.class),
                mock(EmployeeRepository.class), mock(TeamMemberRepository.class), mock(AuditLogService.class),
                mock(NotificationService.class))
                .get(resourceInOrganizationB));

        verify(repository).findByIdAndOrganizationId(resourceInOrganizationB, organizationA);
    }

    @Test
    void attendanceLookupIsOrganizationScoped() {
        setCurrentUser();
        AttendanceRepository repository = mock(AttendanceRepository.class);
        when(repository.findByIdAndOrganizationId(resourceInOrganizationB, organizationA)).thenReturn(Optional.empty());

        assertThrows(BusinessException.class, () -> new AttendanceService(repository, mock(EmployeeRepository.class),
                mock(AuditLogService.class)).get(resourceInOrganizationB));

        verify(repository).findByIdAndOrganizationId(resourceInOrganizationB, organizationA);
    }

    @Test
    void leaveLookupIsOrganizationScoped() {
        setCurrentUser();
        LeaveRequestRepository repository = mock(LeaveRequestRepository.class);
        when(repository.findByIdAndOrganizationId(resourceInOrganizationB, organizationA)).thenReturn(Optional.empty());

        assertThrows(BusinessException.class, () -> new LeaveService(repository, mock(LeaveTypeRepository.class),
                mock(LeaveBalanceRepository.class), mock(EmployeeRepository.class), mock(AuditLogService.class),
                mock(NotificationService.class))
                .get(resourceInOrganizationB));

        verify(repository).findByIdAndOrganizationId(resourceInOrganizationB, organizationA);
    }

    @Test
    void eodLookupIsOrganizationScoped() {
        setCurrentUser();
        EodReportRepository repository = mock(EodReportRepository.class);
        when(repository.findByIdAndOrganizationId(resourceInOrganizationB, organizationA)).thenReturn(Optional.empty());

        assertThrows(BusinessException.class, () -> new EodService(repository, mock(EmployeeRepository.class),
                mock(TeamMemberRepository.class), mock(AuditLogService.class),
                mock(NotificationService.class)).get(resourceInOrganizationB));

        verify(repository).findByIdAndOrganizationId(resourceInOrganizationB, organizationA);
    }

    @Test
    void conversationLookupIsOrganizationScopedBeforeMembershipLookup() {
        setCurrentUser();
        ConversationRepository repository = mock(ConversationRepository.class);
        when(repository.findByIdAndOrganizationId(resourceInOrganizationB, organizationA)).thenReturn(Optional.empty());
        ConversationParticipantRepository participants = mock(ConversationParticipantRepository.class);

        assertThrows(BusinessException.class, () -> new ConversationService(repository, participants,
                mock(MessageRepository.class), mock(UserRepository.class)).get(resourceInOrganizationB));

        verify(repository).findByIdAndOrganizationId(resourceInOrganizationB, organizationA);
        org.mockito.Mockito.verifyNoInteractions(participants);
    }

    @Test
    void nonParticipantCannotReadConversationMessages() {
        setCurrentUser();
        ConversationRepository conversations = mock(ConversationRepository.class);
        ConversationParticipantRepository participants = mock(ConversationParticipantRepository.class);
        MessageRepository messages = mock(MessageRepository.class);
        when(conversations.findByIdAndOrganizationId(resourceInOrganizationB, organizationA))
                .thenReturn(Optional.of(new Conversation(organizationA, null, tenantUser.getId())));
        when(participants.existsByIdConversationIdAndIdUserId(resourceInOrganizationB, tenantUser.getId()))
                .thenReturn(false);

        assertThrows(BusinessException.class, () -> new ConversationService(conversations, participants,
                messages, mock(UserRepository.class)).messages(resourceInOrganizationB, PageRequest.of(0, 50)));

        org.mockito.Mockito.verifyNoInteractions(messages);
    }

    @Test
    void announcementLookupIsScopedToOrganizationAndPlatformScope() {
        setCurrentUser();
        AnnouncementRepository repository = mock(AnnouncementRepository.class);
        when(repository.findVisibleById(resourceInOrganizationB, organizationA, Announcement.Scope.PLATFORM))
                .thenReturn(Optional.empty());

        assertThrows(BusinessException.class, () -> new AnnouncementService(repository,
                mock(NotificationRepository.class), mock(UserRepository.class), mock(TeamRepository.class),
                mock(TeamMemberRepository.class), mock(DepartmentRepository.class), mock(EmployeeRepository.class),
                mock(AuditLogService.class)).get(resourceInOrganizationB));

        verify(repository).findVisibleById(resourceInOrganizationB, organizationA, Announcement.Scope.PLATFORM);
    }

    @Test
    void notificationLookupIsScopedToAuthenticatedRecipient() {
        setCurrentUser();
        NotificationRepository repository = mock(NotificationRepository.class);
        when(repository.findByIdAndUserId(resourceInOrganizationB, tenantUser.getId())).thenReturn(Optional.empty());

        assertThrows(BusinessException.class, () -> new NotificationService(repository, mock(UserRepository.class))
                .markRead(resourceInOrganizationB));

        verify(repository).findByIdAndUserId(resourceInOrganizationB, tenantUser.getId());
    }

    @Test
    void unreadNotificationCountIsScopedToAuthenticatedRecipient() {
        setCurrentUser();
        NotificationRepository repository = mock(NotificationRepository.class);
        when(repository.countByUserIdAndReadAtIsNull(tenantUser.getId())).thenReturn(2L);

        org.junit.jupiter.api.Assertions.assertEquals(2L,
                new NotificationService(repository, mock(UserRepository.class)).unreadCount());

        verify(repository).countByUserIdAndReadAtIsNull(tenantUser.getId());
    }

    @Test
    void notificationCannotBeCreatedForAnotherOrganizationsUser() {
        NotificationRepository notifications = mock(NotificationRepository.class);
        UserRepository users = mock(UserRepository.class);
        when(users.existsByIdAndOrganizationId(resourceInOrganizationB, organizationA)).thenReturn(false);

        assertThrows(BusinessException.class, () -> new NotificationService(notifications, users)
                .notifyUser(organizationA, resourceInOrganizationB, "TASK_ASSIGNED", "Task assigned",
                        "A task has been assigned to you."));

        org.mockito.Mockito.verifyNoInteractions(notifications);
    }

    @Test
    void reportAggregationAlwaysBindsAuthenticatedOrganization() {
        setCurrentUser();
        ReportRepository repository = mock(ReportRepository.class);
        LocalDate from = LocalDate.of(2026, 1, 1);
        LocalDate to = LocalDate.of(2026, 1, 31);

        new ReportService(repository).attendance(from, to, resourceInOrganizationB, null, null, null);

        verify(repository).attendance(organizationA, from, to, resourceInOrganizationB, null, null, null);
    }

    @Test
    void settingsLookupUsesAuthenticatedOrganizationRatherThanRequestData() {
        setCurrentUser();
        CompanySettingsRepository repository = mock(CompanySettingsRepository.class);
        when(repository.findById(organizationA)).thenReturn(Optional.empty());

        new CompanySettingsService(repository, mock(AuditLogService.class)).get();

        verify(repository).findById(organizationA);
    }

    private void setCurrentUser() {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(tenantUser, null, tenantUser.getAuthorities()));
    }
}
