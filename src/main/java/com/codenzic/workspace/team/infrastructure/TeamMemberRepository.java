package com.codenzic.workspace.team.infrastructure;

import com.codenzic.workspace.team.domain.TeamMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TeamMemberRepository extends JpaRepository<TeamMember, UUID> {
    List<TeamMember> findAllByTeamIdAndOrganizationIdOrderByCreatedAt(UUID teamId, UUID organizationId);
    List<TeamMember> findAllByEmployeeIdAndOrganizationId(UUID employeeId, UUID organizationId);
    Optional<TeamMember> findByTeamIdAndEmployeeIdAndOrganizationId(UUID teamId, UUID employeeId, UUID organizationId);
}
