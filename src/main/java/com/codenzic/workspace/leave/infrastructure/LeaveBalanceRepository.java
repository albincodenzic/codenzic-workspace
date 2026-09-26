package com.codenzic.workspace.leave.infrastructure;

import com.codenzic.workspace.leave.domain.LeaveBalance;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface LeaveBalanceRepository extends JpaRepository<LeaveBalance, UUID> {
    List<LeaveBalance> findAllByOrganizationIdAndEmployeeIdAndBalanceYear(UUID organizationId, UUID employeeId, int balanceYear);
    Optional<LeaveBalance> findByOrganizationIdAndEmployeeIdAndLeaveTypeIdAndBalanceYear(UUID organizationId, UUID employeeId, UUID leaveTypeId, int balanceYear);
}
