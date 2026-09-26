package com.codenzic.workspace.communication.application;

import com.codenzic.workspace.audit.application.AuditLogService;
import com.codenzic.workspace.common.exception.BusinessException;
import com.codenzic.workspace.common.security.CurrentUser;
import com.codenzic.workspace.communication.domain.Announcement;
import com.codenzic.workspace.communication.domain.Notification;
import com.codenzic.workspace.communication.infrastructure.AnnouncementRepository;
import com.codenzic.workspace.communication.infrastructure.NotificationRepository;
import com.codenzic.workspace.communication.presentation.dto.CommunicationDtos.AnnouncementRequest;
import com.codenzic.workspace.communication.presentation.dto.CommunicationDtos.AnnouncementResponse;
import com.codenzic.workspace.department.infrastructure.DepartmentRepository;
import com.codenzic.workspace.employee.domain.Employee;
import com.codenzic.workspace.employee.infrastructure.EmployeeRepository;
import com.codenzic.workspace.identity.domain.AccountStatus;
import com.codenzic.workspace.identity.domain.User;
import com.codenzic.workspace.identity.infrastructure.UserRepository;
import com.codenzic.workspace.team.infrastructure.TeamMemberRepository;
import com.codenzic.workspace.team.infrastructure.TeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AnnouncementService {
    private final AnnouncementRepository announcements;
    private final NotificationRepository notifications;
    private final UserRepository users;
    private final TeamRepository teams;
    private final TeamMemberRepository teamMembers;
    private final DepartmentRepository departments;
    private final EmployeeRepository employees;
    private final AuditLogService audit;

    @Transactional
    public AnnouncementResponse create(AnnouncementRequest request) {
        User user = CurrentUser.required();
        UUID organizationId = request.scope() == Announcement.Scope.PLATFORM
                ? null : CurrentUser.requiredOrganizationId();
        if (request.scope() == Announcement.Scope.PLATFORM && !user.isPlatformUser()) {
            throw error("PLATFORM_REQUIRED", "Only platform users can create platform announcements", HttpStatus.FORBIDDEN);
        }
        if (request.scope() == Announcement.Scope.ORGANIZATION && organizationId == null) {
            throw error("ORGANIZATION_REQUIRED", "Organization membership is required", HttpStatus.FORBIDDEN);
        }
        validateTargets(request, organizationId);
        Announcement announcement = announcements.save(new Announcement(organizationId, request.scope(),
                request.title(), request.content(), user.getId(), request.teamId(), request.departmentId()));
        audit.record(organizationId, user.getId(), "CREATE", "ANNOUNCEMENT", announcement.getId(), "{}");
        return to(announcement);
    }

    @Transactional(readOnly = true)
    public List<AnnouncementResponse> list() {
        User user = CurrentUser.required();
        List<Announcement> visible;
        if (user.isPlatformUser()) {
            visible = announcements.findAllByStatusOrderByPublishedAtDesc("PUBLISHED");
        } else if (user.getOrganizationId() == null) {
            return List.of();
        } else {
            visible = new java.util.ArrayList<>(announcements.findAllByScopeAndStatusOrderByPublishedAtDesc(
                    Announcement.Scope.PLATFORM, "PUBLISHED"));
            visible.addAll(announcements.findAllByOrganizationIdAndStatusOrderByPublishedAtDesc(
                    user.getOrganizationId(), "PUBLISHED"));
        }
        if (user.isPlatformUser() && CurrentUser.hasPermission("ANNOUNCEMENT_UPDATE")) {
            visible.addAll(announcements.findAllByStatusOrderByPublishedAtDesc("DRAFT"));
            visible.addAll(announcements.findAllByStatusOrderByPublishedAtDesc("ARCHIVED"));
        } else if (user.getOrganizationId() != null && CurrentUser.hasPermission("ANNOUNCEMENT_UPDATE")) {
            visible.addAll(announcements.findAllByOrganizationIdAndStatusOrderByPublishedAtDesc(
                    user.getOrganizationId(), "DRAFT"));
            visible.addAll(announcements.findAllByOrganizationIdAndStatusOrderByPublishedAtDesc(
                    user.getOrganizationId(), "ARCHIVED"));
        } else if (user.getOrganizationId() != null) {
            visible.addAll(announcements.findAllByOrganizationIdAndCreatedByAndStatusOrderByPublishedAtDesc(
                    user.getOrganizationId(), user.getId(), "DRAFT"));
            visible.addAll(announcements.findAllByOrganizationIdAndCreatedByAndStatusOrderByPublishedAtDesc(
                    user.getOrganizationId(), user.getId(), "ARCHIVED"));
        }
        return visible.stream().filter(this::canSee).map(this::to).toList();
    }

    @Transactional(readOnly = true)
    public AnnouncementResponse get(UUID id) {
        Announcement announcement = find(id);
        if (!canSee(announcement)) {
            throw error("ANNOUNCEMENT_NOT_FOUND", "Announcement not found", HttpStatus.NOT_FOUND);
        }
        return to(announcement);
    }

    @Transactional
    public AnnouncementResponse update(UUID id, AnnouncementRequest request) {
        Announcement announcement = find(id);
        requireManagerOrCreator(announcement);
        validateTargets(request, announcement.getOrganizationId());
        if (request.scope() != announcement.getScope()) {
            throw error("ANNOUNCEMENT_SCOPE_IMMUTABLE", "Announcement scope cannot be changed", HttpStatus.BAD_REQUEST);
        }
        try {
            announcement.update(request.title(), request.content(), request.teamId(), request.departmentId());
        } catch (IllegalStateException exception) {
            throw error("INVALID_TRANSITION", exception.getMessage(), HttpStatus.CONFLICT);
        }
        Announcement updated = announcements.save(announcement);
        audit.record(updated.getOrganizationId(), CurrentUser.id(), "UPDATE", "ANNOUNCEMENT", updated.getId(), "{}");
        return to(updated);
    }

    @Transactional
    public AnnouncementResponse publish(UUID id) {
        Announcement announcement = find(id);
        requireManagerOrCreator(announcement);
        try {
            announcement.publish();
        } catch (IllegalStateException exception) {
            throw error("INVALID_TRANSITION", exception.getMessage(), HttpStatus.CONFLICT);
        }
        Announcement published = announcements.save(announcement);
        audit.record(published.getOrganizationId(), CurrentUser.id(), "PUBLISH", "ANNOUNCEMENT", published.getId(), "{}");
        List<User> recipients = recipients(published);
        notifications.saveAll(recipients.stream().map(user -> new Notification(
                user.getId(), published.getOrganizationId(), "ANNOUNCEMENT", published.getTitle(),
                published.getContent(), published.getId())).toList());
        return to(published);
    }

    @Transactional
    public AnnouncementResponse archive(UUID id) {
        Announcement announcement = find(id);
        requireManagerOrCreator(announcement);
        try {
            announcement.archive();
        } catch (IllegalStateException exception) {
            throw error("INVALID_TRANSITION", exception.getMessage(), HttpStatus.CONFLICT);
        }
        Announcement archived = announcements.save(announcement);
        audit.record(archived.getOrganizationId(), CurrentUser.id(), "ARCHIVE", "ANNOUNCEMENT", archived.getId(), "{}");
        return to(archived);
    }

    private Announcement find(UUID id) {
        User user = CurrentUser.required();
        if (user.isPlatformUser()) {
            return announcements.findById(id)
                    .orElseThrow(() -> error("ANNOUNCEMENT_NOT_FOUND", "Announcement not found", HttpStatus.NOT_FOUND));
        }
        if (user.getOrganizationId() == null) {
            throw error("ANNOUNCEMENT_NOT_FOUND", "Announcement not found", HttpStatus.NOT_FOUND);
        }
        return announcements.findVisibleById(id, user.getOrganizationId(), Announcement.Scope.PLATFORM)
                .orElseThrow(() -> error("ANNOUNCEMENT_NOT_FOUND", "Announcement not found", HttpStatus.NOT_FOUND));
    }

    private void requireManagerOrCreator(Announcement announcement) {
        User user = CurrentUser.required();
        if (announcement.getScope() == Announcement.Scope.PLATFORM && !user.isPlatformUser()) {
            throw error("PLATFORM_REQUIRED", "Only platform users can manage platform announcements", HttpStatus.FORBIDDEN);
        }
        if (!user.isPlatformUser() && !user.getId().equals(announcement.getCreatedBy())
                && !CurrentUser.hasPermission("ANNOUNCEMENT_UPDATE")) {
            throw error("ACCESS_DENIED", "You cannot manage this announcement", HttpStatus.FORBIDDEN);
        }
    }

    private void validateTargets(AnnouncementRequest request, UUID organizationId) {
        if (request.scope() == Announcement.Scope.PLATFORM
                && (request.teamId() != null || request.departmentId() != null)) {
            throw error("INVALID_ANNOUNCEMENT_TARGET", "Platform announcements cannot target tenant teams or departments", HttpStatus.BAD_REQUEST);
        }
        if (request.teamId() != null) {
            var team = teams.findByIdAndOrganizationId(request.teamId(), organizationId)
                    .orElseThrow(() -> error("TEAM_NOT_FOUND", "Team not found in this organization", HttpStatus.BAD_REQUEST));
            if (request.departmentId() != null && !request.departmentId().equals(team.getDepartmentId())) {
                throw error("INVALID_ANNOUNCEMENT_TARGET", "Team must belong to the selected department", HttpStatus.BAD_REQUEST);
            }
        }
        if (request.departmentId() != null && !departments.existsByIdAndOrganizationId(request.departmentId(), organizationId)) {
            throw error("DEPARTMENT_NOT_FOUND", "Department not found in this organization", HttpStatus.BAD_REQUEST);
        }
    }

    private List<User> recipients(Announcement announcement) {
        if (announcement.getScope() == Announcement.Scope.PLATFORM) {
            return users.findAllByAccountStatus(AccountStatus.ACTIVE);
        }
        UUID organizationId = announcement.getOrganizationId();
        if (announcement.getTeamId() == null && announcement.getDepartmentId() == null) {
            return users.findAllByOrganizationIdAndAccountStatus(organizationId, AccountStatus.ACTIVE);
        }
        Set<UUID> teamIds = new HashSet<>();
        if (announcement.getTeamId() != null) {
            teamIds.add(announcement.getTeamId());
        } else {
            teams.findAllByDepartmentIdAndOrganizationId(announcement.getDepartmentId(), organizationId)
                    .forEach(team -> teamIds.add(team.getId()));
        }
        Set<UUID> employeeIds = new HashSet<>();
        teamIds.forEach(teamId -> teamMembers.findAllByTeamIdAndOrganizationIdOrderByCreatedAt(teamId, organizationId)
                .forEach(member -> employeeIds.add(member.getEmployeeId())));
        if (employeeIds.isEmpty()) {
            return List.of();
        }
        List<Employee> targetedEmployees = employees.findAllByOrganizationIdAndIdIn(organizationId, List.copyOf(employeeIds));
        Set<UUID> userIds = targetedEmployees.stream().map(Employee::getUserId).collect(java.util.stream.Collectors.toSet());
        return users.findAllById(userIds).stream().filter(User::isEnabled).toList();
    }

    private boolean canSee(Announcement announcement) {
        if (!"PUBLISHED".equals(announcement.getStatus())) {
            return announcement.getCreatedBy().equals(CurrentUser.id())
                    || CurrentUser.hasPermission("ANNOUNCEMENT_UPDATE");
        }
        if (CurrentUser.hasPermission("ANNOUNCEMENT_UPDATE") || CurrentUser.required().isPlatformUser()) {
            return true;
        }
        if (announcement.getTeamId() == null && announcement.getDepartmentId() == null) {
            return true;
        }
        if (CurrentUser.organizationId() == null) {
            return false;
        }
        UUID employeeId = employees.findByUserIdAndOrganizationId(CurrentUser.id(), CurrentUser.organizationId())
                .map(Employee::getId).orElse(null);
        if (employeeId == null) {
            return false;
        }
        List<UUID> memberTeamIds = teamMembers.findAllByEmployeeIdAndOrganizationId(employeeId, CurrentUser.organizationId())
                .stream().map(member -> member.getTeamId()).toList();
        if (announcement.getTeamId() != null && memberTeamIds.contains(announcement.getTeamId())) {
            return true;
        }
        return announcement.getDepartmentId() != null && teams.findAllByDepartmentIdAndOrganizationId(
                        announcement.getDepartmentId(), CurrentUser.organizationId()).stream()
                .anyMatch(team -> memberTeamIds.contains(team.getId()));
    }

    private AnnouncementResponse to(Announcement announcement) {
        return new AnnouncementResponse(announcement.getId(), announcement.getOrganizationId(), announcement.getScope(),
                announcement.getTitle(), announcement.getContent(), announcement.getCreatedBy(), announcement.getStatus(),
                announcement.getTeamId(), announcement.getDepartmentId(), announcement.getPublishedAt());
    }

    private BusinessException error(String code, String message, HttpStatus status) {
        return new BusinessException(code, message, status);
    }
}
