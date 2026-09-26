package com.codenzic.workspace.department.application;

import com.codenzic.workspace.common.exception.BusinessException;
import com.codenzic.workspace.common.security.CurrentUser;
import com.codenzic.workspace.department.domain.Department;
import com.codenzic.workspace.department.domain.DepartmentStatus;
import com.codenzic.workspace.department.infrastructure.DepartmentRepository;
import com.codenzic.workspace.department.presentation.dto.DepartmentRequest;
import com.codenzic.workspace.department.presentation.dto.DepartmentResponse;
import lombok.RequiredArgsConstructor;
import com.codenzic.workspace.employee.domain.Employee;
import com.codenzic.workspace.employee.infrastructure.EmployeeRepository;
import com.codenzic.workspace.employee.presentation.dto.EmployeeResponse;
import com.codenzic.workspace.team.domain.Team;
import com.codenzic.workspace.team.domain.TeamMember;
import com.codenzic.workspace.team.infrastructure.TeamMemberRepository;
import com.codenzic.workspace.team.infrastructure.TeamRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DepartmentService {
    private final DepartmentRepository repository;
    private final TeamRepository teams;
    private final TeamMemberRepository teamMembers;
    private final EmployeeRepository employees;

    @Transactional
    public DepartmentResponse create(DepartmentRequest request) {
        UUID organizationId = CurrentUser.requiredOrganizationId();
        return to(repository.save(new Department(organizationId, request.name(), request.description())));
    }

    @Transactional(readOnly = true)
    public List<DepartmentResponse> list() {
        return repository.findAllByOrganizationId(CurrentUser.requiredOrganizationId())
                .stream().map(this::to).toList();
    }

    @Transactional(readOnly = true)
    public DepartmentResponse get(UUID id) {
        return to(find(id));
    }

    @Transactional
    public DepartmentResponse update(UUID id, DepartmentRequest request) {
        Department department = find(id);
        department.update(request.name(), request.description());
        return to(repository.save(department));
    }

    @Transactional
    public DepartmentResponse changeStatus(UUID id, DepartmentStatus status) {
        Department department = find(id);
        department.changeStatus(status);
        return to(repository.save(department));
    }

    @Transactional(readOnly = true)
    public List<EmployeeResponse> members(UUID id) {
        Department department = find(id);
        List<Team> departmentTeams = teams.findAllByDepartmentIdAndOrganizationId(id, department.getOrganizationId());
        Set<UUID> teamIds = departmentTeams.stream().map(Team::getId).collect(Collectors.toSet());
        if (teamIds.isEmpty()) {
            return List.of();
        }
        Set<UUID> employeeIds = teamIds.stream()
                .flatMap(teamId -> teamMembers.findAllByTeamIdAndOrganizationIdOrderByCreatedAt(
                        teamId, department.getOrganizationId()).stream())
                .map(TeamMember::getEmployeeId)
                .collect(Collectors.toSet());
        return employees.findAllByOrganizationIdAndIdIn(department.getOrganizationId(), List.copyOf(employeeIds))
                .stream().map(this::toEmployee).toList();
    }

    private Department find(UUID id) {
        return repository.findByIdAndOrganizationId(id, CurrentUser.requiredOrganizationId())
                .orElseThrow(() -> new BusinessException("DEPARTMENT_NOT_FOUND", "Department not found", HttpStatus.NOT_FOUND));
    }

    private DepartmentResponse to(Department department) {
        return new DepartmentResponse(department.getId(), department.getOrganizationId(), department.getName(),
                department.getDescription(), department.getStatus());
    }

    private EmployeeResponse toEmployee(Employee employee) {
        return new EmployeeResponse(employee.getId(), employee.getOrganizationId(), employee.getUserId(),
                employee.getJobTitle(), employee.getPhone(), employee.isActive(), employee.getStatus(), employee.getDateOfBirth());
    }
}
