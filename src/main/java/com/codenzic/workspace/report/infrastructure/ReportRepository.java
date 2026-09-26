package com.codenzic.workspace.report.infrastructure;

import com.codenzic.workspace.attendance.domain.Attendance;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface ReportRepository extends Repository<Attendance, UUID> {
    @Query(value = """
            SELECT a.attendance_date, a.status, count(*)
            FROM attendance_records a
            WHERE a.organization_id = :organizationId
              AND a.attendance_date BETWEEN :from AND :to
              AND (:employeeId IS NULL OR a.employee_id = :employeeId)
              AND (:status IS NULL OR a.status = :status)
              AND (:teamId IS NULL OR EXISTS (
                   SELECT 1 FROM team_members tm
                   WHERE tm.organization_id = :organizationId AND tm.employee_id = a.employee_id
                     AND tm.team_id = :teamId))
              AND (:departmentId IS NULL OR EXISTS (
                   SELECT 1 FROM team_members tm JOIN teams t ON t.id = tm.team_id
                   WHERE tm.organization_id = :organizationId AND tm.employee_id = a.employee_id
                     AND t.organization_id = :organizationId AND t.department_id = :departmentId))
            GROUP BY a.attendance_date, a.status ORDER BY a.attendance_date, a.status
            """, nativeQuery = true)
    List<Object[]> attendance(
            @Param("organizationId") UUID organizationId, @Param("from") LocalDate from, @Param("to") LocalDate to,
            @Param("employeeId") UUID employeeId, @Param("departmentId") UUID departmentId,
            @Param("teamId") UUID teamId, @Param("status") String status);

    @Query(value = """
            SELECT l.leave_type, l.status, count(*)
            FROM leave_requests l
            WHERE l.organization_id = :organizationId AND l.start_date <= :to AND l.end_date >= :from
              AND (:employeeId IS NULL OR l.employee_id = :employeeId)
              AND (:status IS NULL OR l.status = :status)
              AND (:departmentId IS NULL OR EXISTS (
                   SELECT 1 FROM team_members tm JOIN teams t ON t.id = tm.team_id
                   WHERE tm.organization_id = :organizationId AND tm.employee_id = l.employee_id
                     AND t.organization_id = :organizationId AND t.department_id = :departmentId))
              AND (:teamId IS NULL OR EXISTS (
                   SELECT 1 FROM team_members tm WHERE tm.organization_id = :organizationId
                     AND tm.employee_id = l.employee_id AND tm.team_id = :teamId))
            GROUP BY l.leave_type, l.status ORDER BY l.leave_type, l.status
            """, nativeQuery = true)
    List<Object[]> leave(
            @Param("organizationId") UUID organizationId, @Param("from") LocalDate from, @Param("to") LocalDate to,
            @Param("employeeId") UUID employeeId, @Param("departmentId") UUID departmentId,
            @Param("teamId") UUID teamId, @Param("status") String status);

    @Query(value = """
            SELECT e.employment_status, count(*)
            FROM employees e
            WHERE e.organization_id = :organizationId
              AND (:employeeId IS NULL OR e.id = :employeeId)
              AND (:status IS NULL OR e.employment_status = :status)
              AND (:departmentId IS NULL OR EXISTS (
                   SELECT 1 FROM team_members tm JOIN teams t ON t.id = tm.team_id
                   WHERE tm.organization_id = :organizationId AND tm.employee_id = e.id
                     AND t.organization_id = :organizationId AND t.department_id = :departmentId))
              AND (:teamId IS NULL OR EXISTS (
                   SELECT 1 FROM team_members tm WHERE tm.organization_id = :organizationId
                     AND tm.employee_id = e.id AND tm.team_id = :teamId))
            GROUP BY e.employment_status ORDER BY e.employment_status
            """, nativeQuery = true)
    List<Object[]> employees(
            @Param("organizationId") UUID organizationId, @Param("employeeId") UUID employeeId,
            @Param("departmentId") UUID departmentId, @Param("teamId") UUID teamId,
            @Param("status") String status);

    @Query(value = """
            SELECT p.id, p.name, p.status, p.progress, count(t.id)
            FROM projects p LEFT JOIN tasks t ON t.project_id = p.id AND t.organization_id = p.organization_id
            WHERE p.organization_id = :organizationId
              AND (:status IS NULL OR p.status = :status)
              AND (:projectId IS NULL OR p.id = :projectId)
              AND (:employeeId IS NULL OR p.manager_id = :employeeId OR EXISTS (
                   SELECT 1 FROM project_members pm WHERE pm.organization_id = :organizationId
                     AND pm.project_id = p.id AND pm.employee_id = :employeeId))
            GROUP BY p.id, p.name, p.status, p.progress ORDER BY p.name
            """, nativeQuery = true)
    List<Object[]> projects(
            @Param("organizationId") UUID organizationId, @Param("projectId") UUID projectId,
            @Param("employeeId") UUID employeeId, @Param("status") String status);

    @Query(value = """
            SELECT t.status, t.priority, count(*)
            FROM tasks t
            WHERE t.organization_id = :organizationId
              AND (:projectId IS NULL OR t.project_id = :projectId)
              AND (:employeeId IS NULL OR t.assignee_id = :employeeId)
              AND (:status IS NULL OR t.status = :status)
              AND (:priority IS NULL OR t.priority = :priority)
              AND (:departmentId IS NULL OR EXISTS (
                   SELECT 1 FROM team_members tm JOIN teams team ON team.id = tm.team_id
                   WHERE tm.organization_id = :organizationId AND tm.employee_id = t.assignee_id
                     AND team.organization_id = :organizationId AND team.department_id = :departmentId))
              AND (:teamId IS NULL OR t.team_id = :teamId)
            GROUP BY t.status, t.priority ORDER BY t.status, t.priority
            """, nativeQuery = true)
    List<Object[]> tasks(
            @Param("organizationId") UUID organizationId, @Param("projectId") UUID projectId,
            @Param("employeeId") UUID employeeId, @Param("departmentId") UUID departmentId,
            @Param("teamId") UUID teamId, @Param("status") String status, @Param("priority") String priority);

    @Query(value = """
            SELECT e.status, count(*)
            FROM eod_reports e
            WHERE e.organization_id = :organizationId
              AND e.report_date BETWEEN :from AND :to
              AND (:employeeId IS NULL OR e.employee_id = :employeeId)
              AND (:status IS NULL OR e.status = :status)
              AND (:teamId IS NULL OR EXISTS (
                   SELECT 1 FROM team_members tm WHERE tm.organization_id = :organizationId
                     AND tm.employee_id = e.employee_id AND tm.team_id = :teamId))
            GROUP BY e.status ORDER BY e.status
            """, nativeQuery = true)
    List<Object[]> eod(
            @Param("organizationId") UUID organizationId, @Param("from") LocalDate from,
            @Param("to") LocalDate to, @Param("employeeId") UUID employeeId,
            @Param("teamId") UUID teamId, @Param("status") String status);
}
