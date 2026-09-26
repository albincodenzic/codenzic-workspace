package com.codenzic.workspace.leave.infrastructure;

import com.codenzic.workspace.leave.domain.LeaveType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface LeaveTypeRepository extends JpaRepository<LeaveType, UUID> {
    List<LeaveType> findAllByOrganizationIdOrderByName(UUID organizationId);
    Optional<LeaveType> findByIdAndOrganizationId(UUID id, UUID organizationId);
    Optional<LeaveType> findByCodeIgnoreCaseAndOrganizationId(String code, UUID organizationId);
}
