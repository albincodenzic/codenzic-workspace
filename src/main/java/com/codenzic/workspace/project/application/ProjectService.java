package com.codenzic.workspace.project.application;

import com.codenzic.workspace.audit.application.AuditLogService;
import com.codenzic.workspace.common.api.PageResponse;
import com.codenzic.workspace.common.exception.BusinessException;
import com.codenzic.workspace.common.security.CurrentUser;
import com.codenzic.workspace.employee.domain.Employee;
import com.codenzic.workspace.employee.infrastructure.EmployeeRepository;
import com.codenzic.workspace.project.domain.Project;
import com.codenzic.workspace.project.domain.ProjectMember;
import com.codenzic.workspace.project.infrastructure.ProjectMemberRepository;
import com.codenzic.workspace.project.infrastructure.ProjectRepository;
import com.codenzic.workspace.project.presentation.dto.ProjectMemberRequest;
import com.codenzic.workspace.project.presentation.dto.ProjectMemberResponse;
import com.codenzic.workspace.project.presentation.dto.ProjectRequest;
import com.codenzic.workspace.project.presentation.dto.ProjectResponse;
import com.codenzic.workspace.project.presentation.dto.ProjectStatusRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProjectService {
    private final ProjectRepository repository;
    private final ProjectMemberRepository members;
    private final EmployeeRepository employeeRepository;
    private final AuditLogService audit;

    @Transactional
    public ProjectResponse create(ProjectRequest request) {
        UUID organizationId = CurrentUser.requiredOrganizationId();
        validateDates(request.startDate(), request.dueDate());
        validateEmployee(request.managerId(), organizationId);
        String status = request.status() == null ? "ACTIVE" : request.status();
        int progress = request.progress() == null ? 0 : request.progress();
        Project project = repository.save(new Project(organizationId, request.name(), request.description(), status,
                request.startDate(), request.dueDate(), request.managerId(), progress));
        audit.record(organizationId, CurrentUser.id(), "CREATE", "PROJECT", project.getId(), "{}");
        return to(project);
    }

    @Transactional(readOnly = true)
    public PageResponse<ProjectResponse> list(String search, String status, UUID employeeId, Pageable pageable) {
        UUID organizationId = CurrentUser.requiredOrganizationId();
        if (CurrentUser.hasPermission("PROJECT_READ")) {
            validateEmployee(employeeId, organizationId);
        } else {
            employeeId = currentEmployee(organizationId).getId();
        }
        String term = search == null || search.isBlank() ? null : "%" + search.trim().toLowerCase() + "%";
        String statusFilter = status == null || status.isBlank() ? null : status.trim().toUpperCase();
        return PageResponse.from(repository.search(organizationId, term, statusFilter, employeeId, pageable).map(this::to));
    }

    @Transactional(readOnly = true)
    public ProjectResponse get(UUID id) {
        return to(find(id));
    }

    @Transactional
    public ProjectResponse update(UUID id, ProjectRequest request) {
        UUID organizationId = CurrentUser.requiredOrganizationId();
        validateDates(request.startDate(), request.dueDate());
        validateEmployee(request.managerId(), organizationId);
        Project project = find(id);
        project.update(request.name(), request.description(), request.startDate(), request.dueDate(),
                request.managerId(), request.progress() == null ? 0 : request.progress());
        Project updated = repository.save(project);
        audit.record(organizationId, CurrentUser.id(), "UPDATE", "PROJECT", updated.getId(), "{}");
        return to(updated);
    }

    @Transactional
    public ProjectResponse changeStatus(UUID id, ProjectStatusRequest request) {
        Project project = find(id);
        String previousStatus = project.getStatus();
        project.changeStatus(request.status());
        Project updated = repository.save(project);
        if (!previousStatus.equals(updated.getStatus())) {
            audit.record(updated.getOrganizationId(), CurrentUser.id(), "STATUS_CHANGE", "PROJECT", updated.getId(),
                    "{\"status\":\"" + updated.getStatus() + "\"}");
        }
        return to(updated);
    }

    @Transactional(readOnly = true)
    public List<ProjectMemberResponse> members(UUID id) {
        Project project = find(id);
        return members.findAllByProjectIdAndOrganizationIdOrderByCreatedAt(id, project.getOrganizationId())
                .stream().map(this::toMember).toList();
    }

    @Transactional
    public ProjectMemberResponse addMember(UUID id, ProjectMemberRequest request) {
        Project project = find(id);
        UUID organizationId = project.getOrganizationId();
        validateEmployee(request.employeeId(), organizationId);
        if (members.findByProjectIdAndEmployeeIdAndOrganizationId(id, request.employeeId(), organizationId).isPresent()) {
            throw new BusinessException("PROJECT_MEMBER_EXISTS", "Employee is already a project member", HttpStatus.CONFLICT);
        }
        ProjectMember member = members.save(new ProjectMember(organizationId, id, request.employeeId(), CurrentUser.id()));
        audit.record(organizationId, CurrentUser.id(), "MEMBER_ADDED", "PROJECT", id, "{}");
        return toMember(member);
    }

    @Transactional
    public void removeMember(UUID id, UUID employeeId) {
        Project project = find(id);
        ProjectMember member = members.findByProjectIdAndEmployeeIdAndOrganizationId(
                        id, employeeId, project.getOrganizationId())
                .orElseThrow(() -> new BusinessException("PROJECT_MEMBER_NOT_FOUND", "Project member not found", HttpStatus.NOT_FOUND));
        members.delete(member);
        audit.record(project.getOrganizationId(), CurrentUser.id(), "MEMBER_REMOVED", "PROJECT", id, "{}");
    }

    private Project find(UUID id) {
        UUID organizationId = CurrentUser.requiredOrganizationId();
        Project project = repository.findByIdAndOrganizationId(id, organizationId)
                .orElseThrow(() -> new BusinessException("PROJECT_NOT_FOUND", "Project not found", HttpStatus.NOT_FOUND));
        if (CurrentUser.hasPermission("PROJECT_READ")) {
            return project;
        }
        UUID employeeId = currentEmployee(organizationId).getId();
        if (employeeId.equals(project.getManagerId())
                || members.existsByProjectIdAndEmployeeIdAndOrganizationId(id, employeeId, organizationId)) {
            return project;
        }
        throw new BusinessException("PROJECT_NOT_FOUND", "Project not found", HttpStatus.NOT_FOUND);
    }

    private Employee currentEmployee(UUID organizationId) {
        return employeeRepository.findByUserIdAndOrganizationId(CurrentUser.id(), organizationId)
                .orElseThrow(() -> new BusinessException("EMPLOYEE_NOT_FOUND", "Current user is not an employee", HttpStatus.FORBIDDEN));
    }

    private void validateEmployee(UUID employeeId, UUID organizationId) {
        if (employeeId != null && !employeeRepository.existsByIdAndOrganizationId(employeeId, organizationId)) {
            throw new BusinessException("EMPLOYEE_ORGANIZATION_MISMATCH",
                    "Employee must belong to the current organization", HttpStatus.BAD_REQUEST);
        }
    }

    private void validateDates(LocalDate start, LocalDate due) {
        if (start != null && due != null && due.isBefore(start)) {
            throw new BusinessException("INVALID_PROJECT_DATES", "Due date must not precede start date", HttpStatus.BAD_REQUEST);
        }
    }

    private ProjectResponse to(Project project) {
        return new ProjectResponse(project.getId(), project.getOrganizationId(), project.getName(), project.getDescription(),
                project.getStatus(), project.getStartDate(), project.getDueDate(), project.getManagerId(), project.getProgress());
    }

    private ProjectMemberResponse toMember(ProjectMember member) {
        return new ProjectMemberResponse(member.getId(), member.getProjectId(), member.getEmployeeId(),
                member.getAddedBy(), member.getCreatedAt());
    }
}
