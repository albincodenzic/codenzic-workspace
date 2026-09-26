package com.codenzic.workspace.task.application;

import com.codenzic.workspace.audit.application.AuditLogService;
import com.codenzic.workspace.common.api.PageResponse;
import com.codenzic.workspace.common.exception.BusinessException;
import com.codenzic.workspace.common.security.CurrentUser;
import com.codenzic.workspace.communication.application.NotificationService;
import com.codenzic.workspace.employee.domain.Employee;
import com.codenzic.workspace.employee.infrastructure.EmployeeRepository;
import com.codenzic.workspace.project.infrastructure.ProjectRepository;
import com.codenzic.workspace.task.domain.Task;
import com.codenzic.workspace.task.domain.TaskComment;
import com.codenzic.workspace.task.domain.TaskHistory;
import com.codenzic.workspace.task.infrastructure.TaskCommentRepository;
import com.codenzic.workspace.task.infrastructure.TaskHistoryRepository;
import com.codenzic.workspace.task.infrastructure.TaskRepository;
import com.codenzic.workspace.team.infrastructure.TeamMemberRepository;
import com.codenzic.workspace.task.presentation.dto.*;
import com.codenzic.workspace.team.infrastructure.TeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TaskService {
    private final TaskRepository repository;
    private final TaskCommentRepository comments;
    private final TaskHistoryRepository history;
    private final ProjectRepository projectRepository;
    private final TeamRepository teamRepository;
    private final EmployeeRepository employeeRepository;
    private final TeamMemberRepository teamMembers;
    private final AuditLogService audit;
    private final NotificationService notifications;

    @Transactional
    public TaskResponse create(TaskRequest request) {
        UUID organizationId = CurrentUser.requiredOrganizationId();
        validateReferences(request, organizationId);
        Task task = repository.save(new Task(organizationId, request.projectId(), request.teamId(), request.assigneeId(),
                request.title(), request.description(), defaultValue(request.status(), "TODO"),
                defaultValue(request.priority(), "MEDIUM"), request.dueDate()));
        record(task, "status", null, task.getStatus());
        audit.record(organizationId, CurrentUser.id(), "CREATE", "TASK", task.getId(), "{}");
        return to(task);
    }

    @Transactional(readOnly = true)
    public PageResponse<TaskResponse> list(
            UUID projectId,
            UUID employeeId,
            String status,
            String priority,
            String search,
            Pageable pageable
    ) {
        UUID organizationId = CurrentUser.requiredOrganizationId();
        if (projectId != null && !projectRepository.existsByIdAndOrganizationId(projectId, organizationId)) {
            throw new BusinessException("PROJECT_NOT_FOUND", "Project not found", HttpStatus.NOT_FOUND);
        }
        if (employeeId != null && !employeeRepository.existsByIdAndOrganizationId(employeeId, organizationId)) {
            throw new BusinessException("EMPLOYEE_NOT_FOUND", "Employee not found", HttpStatus.NOT_FOUND);
        }
        String term = search == null || search.isBlank() ? null : "%" + search.trim().toLowerCase(Locale.ROOT) + "%";
        String statusFilter = status == null || status.isBlank() ? null : status.trim().toUpperCase(Locale.ROOT);
        String priorityFilter = priority == null || priority.isBlank() ? null : priority.trim().toUpperCase(Locale.ROOT);
        UUID scopedEmployeeId = employeeId;
        List<UUID> teamIds = null;
        boolean teamScoped = false;
        if (!CurrentUser.hasPermission("TASK_READ")) {
            Employee self = currentEmployee(organizationId);
            if (CurrentUser.hasPermission("TASK_READ_TEAM")) {
                teamIds = teamMembers.findAllByEmployeeIdAndOrganizationId(self.getId(), organizationId)
                        .stream().map(member -> member.getTeamId()).toList();
                if (teamIds.isEmpty()) {
                    return PageResponse.from(org.springframework.data.domain.Page.empty(pageable));
                }
                teamScoped = true;
            } else {
                scopedEmployeeId = self.getId();
            }
        }
        return PageResponse.from(repository.search(organizationId, projectId, scopedEmployeeId, teamScoped,
                teamIds == null ? List.of(new UUID(0L, 0L)) : teamIds, statusFilter, priorityFilter, term, pageable).map(this::to));
    }

    @Transactional(readOnly = true)
    public PageResponse<TaskResponse> myTasks(Pageable pageable) {
        UUID organizationId = CurrentUser.requiredOrganizationId();
        UUID employeeId = currentEmployee(organizationId).getId();
        return PageResponse.from(repository.search(organizationId, null, employeeId, false,
                List.of(new UUID(0L, 0L)), null, null, null, pageable)
                .map(this::to));
    }

    @Transactional(readOnly = true)
    public TaskResponse get(UUID id) {
        return to(find(id));
    }

    @Transactional
    public TaskResponse update(UUID id, TaskRequest request) {
        UUID organizationId = CurrentUser.requiredOrganizationId();
        validateReferences(request, organizationId);
        Task task = find(id);
        String status = defaultValue(request.status(), task.getStatus());
        String priority = defaultValue(request.priority(), task.getPriority());
        recordIfChanged(task, "projectId", task.getProjectId(), request.projectId());
        recordIfChanged(task, "teamId", task.getTeamId(), request.teamId());
        recordIfChanged(task, "assigneeId", task.getAssigneeId(), request.assigneeId());
        recordIfChanged(task, "title", task.getTitle(), request.title());
        recordIfChanged(task, "description", task.getDescription(), request.description());
        recordIfChanged(task, "status", task.getStatus(), status);
        recordIfChanged(task, "priority", task.getPriority(), priority);
        recordIfChanged(task, "dueDate", task.getDueDate(), request.dueDate());
        task.update(request.projectId(), request.teamId(), request.assigneeId(), request.title(),
                request.description(), status, priority, request.dueDate());
        Task updated = repository.save(task);
        audit.record(organizationId, CurrentUser.id(), "UPDATE", "TASK", updated.getId(), "{}");
        return to(updated);
    }

    @Transactional
    public TaskResponse changeStatus(UUID id, TaskStatusRequest request) {
        Task task = find(id);
        if (!Objects.equals(task.getStatus(), request.status())) {
            record(task, "status", task.getStatus(), request.status());
            task.changeStatus(request.status());
            audit.record(task.getOrganizationId(), CurrentUser.id(), "STATUS_CHANGE", "TASK", task.getId(),
                    "{\"status\":\"" + request.status() + "\"}");
        }
        return to(repository.save(task));
    }

    @Transactional
    public TaskResponse changePriority(UUID id, TaskPriorityRequest request) {
        Task task = find(id);
        if (!Objects.equals(task.getPriority(), request.priority())) {
            record(task, "priority", task.getPriority(), request.priority());
            task.changePriority(request.priority());
            audit.record(task.getOrganizationId(), CurrentUser.id(), "PRIORITY_CHANGE", "TASK", task.getId(),
                    "{\"priority\":\"" + request.priority() + "\"}");
        }
        return to(repository.save(task));
    }

    @Transactional
    public TaskResponse assign(UUID id, TaskAssignmentRequest request) {
        UUID organizationId = CurrentUser.requiredOrganizationId();
        Employee assignee = request.assigneeId() == null ? null
                : employeeRepository.findByIdAndOrganizationId(request.assigneeId(), organizationId)
                .orElseThrow(() -> new BusinessException("EMPLOYEE_NOT_FOUND",
                        "Assignee not found in the current organization", HttpStatus.BAD_REQUEST));
        Task task = find(id);
        if (!Objects.equals(task.getAssigneeId(), request.assigneeId())) {
            record(task, "assigneeId", value(task.getAssigneeId()), value(request.assigneeId()));
            task.assign(request.assigneeId());
            audit.record(task.getOrganizationId(), CurrentUser.id(), "ASSIGN", "TASK", task.getId(), "{}");
            if (assignee != null) {
                notifications.notifyUser(organizationId, assignee.getUserId(), "TASK_ASSIGNED",
                        "Task assigned", "A task has been assigned to you.");
            }
        }
        return to(repository.save(task));
    }

    @Transactional(readOnly = true)
    public List<TaskCommentResponse> comments(UUID id) {
        UUID organizationId = CurrentUser.requiredOrganizationId();
        find(id);
        return comments.findAllByTaskIdAndOrganizationIdOrderByCreatedAtAsc(id, organizationId).stream()
                .map(this::to).toList();
    }

    @Transactional
    public TaskCommentResponse addComment(UUID id, TaskCommentRequest request) {
        UUID organizationId = CurrentUser.requiredOrganizationId();
        find(id);
        TaskComment comment = comments.save(new TaskComment(organizationId, id, CurrentUser.id(), request.body()));
        audit.record(organizationId, CurrentUser.id(), "COMMENT", "TASK", id, "{}");
        return to(comment);
    }

    @Transactional(readOnly = true)
    public List<TaskHistoryResponse> history(UUID id) {
        UUID organizationId = CurrentUser.requiredOrganizationId();
        find(id);
        return history.findAllByTaskIdAndOrganizationIdOrderByCreatedAtDesc(id, organizationId).stream()
                .map(this::to).toList();
    }

    private Task find(UUID id) {
        UUID organizationId = CurrentUser.requiredOrganizationId();
        Task task = repository.findByIdAndOrganizationId(id, organizationId)
                .orElseThrow(() -> new BusinessException("TASK_NOT_FOUND", "Task not found", HttpStatus.NOT_FOUND));
        if (CurrentUser.hasPermission("TASK_READ")) {
            return task;
        }
        Employee self = currentEmployee(organizationId);
        boolean assignedToSelf = self.getId().equals(task.getAssigneeId());
        boolean inOwnTeam = task.getTeamId() != null
                && teamMembers.findByTeamIdAndEmployeeIdAndOrganizationId(task.getTeamId(), self.getId(), organizationId)
                .isPresent();
        if ((CurrentUser.hasPermission("TASK_READ_SELF") && assignedToSelf)
                || (CurrentUser.hasPermission("TASK_READ_TEAM") && inOwnTeam)) {
            return task;
        }
        throw new BusinessException("TASK_NOT_FOUND", "Task not found", HttpStatus.NOT_FOUND);
    }

    private Employee currentEmployee(UUID organizationId) {
        return employeeRepository.findByUserIdAndOrganizationId(CurrentUser.id(), organizationId)
                .orElseThrow(() -> new BusinessException("EMPLOYEE_NOT_FOUND", "Current user is not an employee", HttpStatus.FORBIDDEN));
    }

    private void validateReferences(TaskRequest request, UUID organizationId) {
        if (!projectRepository.existsByIdAndOrganizationId(request.projectId(), organizationId)) {
            throw new BusinessException("PROJECT_ORGANIZATION_MISMATCH", "Task project must belong to the current organization", HttpStatus.BAD_REQUEST);
        }
        if (request.teamId() != null && !teamRepository.existsByIdAndOrganizationId(request.teamId(), organizationId)) {
            throw new BusinessException("TEAM_ORGANIZATION_MISMATCH", "Task team must belong to the current organization", HttpStatus.BAD_REQUEST);
        }
        if (request.assigneeId() != null && !employeeRepository.existsByIdAndOrganizationId(request.assigneeId(), organizationId)) {
            throw new BusinessException("EMPLOYEE_ORGANIZATION_MISMATCH", "Task assignee must belong to the current organization", HttpStatus.BAD_REQUEST);
        }
    }

    private void recordIfChanged(Task task, String field, Object previous, Object next) {
        if (!Objects.equals(previous, next)) {
            record(task, field, value(previous), value(next));
        }
    }

    private void record(Task task, String field, String previous, String next) {
        history.save(new TaskHistory(task.getOrganizationId(), task.getId(), CurrentUser.id(), field, previous, next));
    }

    private String value(Object value) {
        return value == null ? null : value.toString();
    }

    private String defaultValue(String value, String fallback) {
        return value == null ? fallback : value;
    }

    private TaskResponse to(Task task) {
        return new TaskResponse(task.getId(), task.getOrganizationId(), task.getProjectId(), task.getTeamId(),
                task.getAssigneeId(), task.getTitle(), task.getDescription(), task.getStatus(), task.getPriority(), task.getDueDate());
    }

    private TaskCommentResponse to(TaskComment comment) {
        return new TaskCommentResponse(comment.getId(), comment.getTaskId(), comment.getAuthorId(), comment.getBody(), comment.getCreatedAt());
    }

    private TaskHistoryResponse to(TaskHistory event) {
        return new TaskHistoryResponse(event.getId(), event.getTaskId(), event.getActorId(), event.getFieldName(),
                event.getOldValue(), event.getNewValue(), event.getCreatedAt());
    }
}
