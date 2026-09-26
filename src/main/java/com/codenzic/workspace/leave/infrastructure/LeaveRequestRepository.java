package com.codenzic.workspace.leave.infrastructure;
import com.codenzic.workspace.leave.domain.LeaveRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.*;
public interface LeaveRequestRepository extends JpaRepository<LeaveRequest,UUID> {
 List<LeaveRequest> findAllByOrganizationIdOrderByStartDateDesc(UUID org);
 List<LeaveRequest> findAllByOrganizationIdAndStatusOrderByStartDateDesc(UUID org, String status);
 Optional<LeaveRequest> findByIdAndOrganizationId(UUID id,UUID org);
 List<LeaveRequest> findAllByOrganizationIdAndEmployeeIdOrderByStartDateDesc(UUID org,UUID employee);
 List<LeaveRequest> findAllByOrganizationIdAndEmployeeIdAndStartDateLessThanEqualAndEndDateGreaterThanEqual(UUID org,UUID employee,LocalDate end,LocalDate start);
 List<LeaveRequest> findAllByOrganizationIdAndStartDateLessThanEqualAndEndDateGreaterThanEqualOrderByStartDateAsc(UUID org,LocalDate end,LocalDate start);
 List<LeaveRequest> findAllByOrganizationIdAndEmployeeIdAndLeaveTypeAndStatusAndStartDateLessThanEqualAndEndDateGreaterThanEqual(UUID org,UUID employee,String leaveType,String status,LocalDate yearEnd,LocalDate yearStart);
}
