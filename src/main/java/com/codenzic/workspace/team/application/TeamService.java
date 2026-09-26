package com.codenzic.workspace.team.application;

import com.codenzic.workspace.common.exception.BusinessException;
import com.codenzic.workspace.common.security.CurrentUser;
import com.codenzic.workspace.department.infrastructure.DepartmentRepository;
import com.codenzic.workspace.employee.infrastructure.EmployeeRepository;
import com.codenzic.workspace.team.domain.Team;
import com.codenzic.workspace.team.domain.TeamMember;
import com.codenzic.workspace.team.infrastructure.TeamMemberRepository;
import com.codenzic.workspace.team.infrastructure.TeamRepository;
import com.codenzic.workspace.team.presentation.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TeamService {
    private final TeamRepository repository;
    private final TeamMemberRepository members;
    private final DepartmentRepository departmentRepository;
    private final EmployeeRepository employeeRepository;

    @Transactional
    public TeamResponse create(TeamRequest request) {
        UUID organizationId = CurrentUser.requiredOrganizationId();
        validateDepartment(request.departmentId(), organizationId);
        return to(repository.save(new Team(organizationId, request.departmentId(), request.name(), request.description())));
    }

    @Transactional(readOnly = true)
    public List<TeamResponse> list() {
        return repository.findAllByOrganizationId(CurrentUser.requiredOrganizationId()).stream().map(this::to).toList();
    }

    @Transactional(readOnly = true)
    public TeamResponse get(UUID id) {
        return to(findTeam(id));
    }

    @Transactional
    public TeamResponse update(UUID id, TeamRequest request) {
        UUID organizationId = CurrentUser.requiredOrganizationId();
        validateDepartment(request.departmentId(), organizationId);
        Team team = findTeam(id);
        team.update(request.departmentId(), request.name(), request.description());
        return to(repository.save(team));
    }

    @Transactional(readOnly = true)
    public List<TeamMemberResponse> members(UUID teamId) {
        UUID organizationId = CurrentUser.requiredOrganizationId();
        findTeam(teamId);
        return members.findAllByTeamIdAndOrganizationIdOrderByCreatedAt(teamId, organizationId)
                .stream().map(this::toMember).toList();
    }

    @Transactional
    public TeamMemberResponse addMember(UUID teamId, TeamMemberRequest request) {
        UUID organizationId = CurrentUser.requiredOrganizationId();
        findTeam(teamId);
        if (!employeeRepository.existsByIdAndOrganizationId(request.employeeId(), organizationId)) {
            throw new BusinessException("EMPLOYEE_NOT_FOUND", "Employee not found in the current organization", HttpStatus.BAD_REQUEST);
        }
        if (members.findByTeamIdAndEmployeeIdAndOrganizationId(teamId, request.employeeId(), organizationId).isPresent()) {
            throw new BusinessException("TEAM_MEMBER_EXISTS", "Employee is already a member of this team", HttpStatus.CONFLICT);
        }
        TeamMember member = members.save(new TeamMember(organizationId, teamId, request.employeeId(), CurrentUser.id()));
        return toMember(member);
    }

    @Transactional
    public void removeMember(UUID teamId, UUID employeeId) {
        UUID organizationId = CurrentUser.requiredOrganizationId();
        findTeam(teamId);
        TeamMember member = members.findByTeamIdAndEmployeeIdAndOrganizationId(teamId, employeeId, organizationId)
                .orElseThrow(() -> new BusinessException("TEAM_MEMBER_NOT_FOUND", "Team member not found", HttpStatus.NOT_FOUND));
        members.delete(member);
    }

    private Team findTeam(UUID id) {
        return repository.findByIdAndOrganizationId(id, CurrentUser.requiredOrganizationId())
                .orElseThrow(() -> new BusinessException("TEAM_NOT_FOUND", "Team not found", HttpStatus.NOT_FOUND));
    }

    private void validateDepartment(UUID departmentId, UUID organizationId) {
        if (departmentId != null && !departmentRepository.existsByIdAndOrganizationId(departmentId, organizationId)) {
            throw new BusinessException("DEPARTMENT_ORGANIZATION_MISMATCH", "Team department must belong to the current organization", HttpStatus.BAD_REQUEST);
        }
    }

    private TeamResponse to(Team team) {
        return new TeamResponse(team.getId(), team.getOrganizationId(), team.getDepartmentId(), team.getName(), team.getDescription());
    }

    private TeamMemberResponse toMember(TeamMember member) {
        return new TeamMemberResponse(member.getId(), member.getTeamId(), member.getEmployeeId(), member.getAddedBy(), member.getCreatedAt());
    }
}
