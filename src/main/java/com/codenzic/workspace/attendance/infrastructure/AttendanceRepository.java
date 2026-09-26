package com.codenzic.workspace.attendance.infrastructure;
import com.codenzic.workspace.attendance.domain.Attendance;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import java.util.*;
public interface AttendanceRepository extends JpaRepository<Attendance,UUID> {
 List<Attendance> findAllByOrganizationIdOrderByAttendanceDateDesc(UUID org);
 Optional<Attendance> findByIdAndOrganizationId(UUID id,UUID org);
 Optional<Attendance> findByEmployeeIdAndAttendanceDate(UUID employee,LocalDate date);
 List<Attendance> findAllByOrganizationIdAndAttendanceDateBetweenOrderByAttendanceDateAsc(UUID org,LocalDate from,LocalDate to);
 List<Attendance> findAllByOrganizationIdAndAttendanceDateOrderByAttendanceDateAsc(UUID org,LocalDate date);
 List<Attendance> findAllByOrganizationIdAndEmployeeIdAndAttendanceDateBetweenOrderByAttendanceDateDesc(UUID org,UUID employee,LocalDate from,LocalDate to);
 boolean existsByEmployeeIdAndAttendanceDate(UUID employee,LocalDate date);

 @Query("""
        select attendance from Attendance attendance
        where attendance.organizationId = :organizationId
          and attendance.attendanceDate = :date
          and (:employeeId is null or attendance.employeeId = :employeeId)
          and (:status is null or attendance.status = :status)
          and (:teamId is null or exists (
               select member.id from TeamMember member
               where member.employeeId = attendance.employeeId and member.teamId = :teamId
                 and member.organizationId = :organizationId))
          and (:departmentId is null or exists (
               select member.id from TeamMember member join Team team on team.id = member.teamId
               where member.employeeId = attendance.employeeId and team.departmentId = :departmentId
                 and member.organizationId = :organizationId and team.organizationId = :organizationId))
        """)
 Page<Attendance> monitor(
         @Param("organizationId") UUID organizationId,
         @Param("date") LocalDate date,
         @Param("employeeId") UUID employeeId,
         @Param("departmentId") UUID departmentId,
         @Param("teamId") UUID teamId,
         @Param("status") String status,
         Pageable pageable
 );
}
