package com.codenzic.workspace.dashboard.infrastructure;

import com.codenzic.workspace.attendance.domain.Attendance;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface DashboardRepository extends Repository<Attendance, UUID> {
    @Query(value = """
            SELECT
              (SELECT count(*) FROM employees WHERE organization_id = :organizationId AND employment_status = 'ACTIVE'),
              (SELECT count(*) FROM projects WHERE organization_id = :organizationId AND status = 'ACTIVE'),
              (SELECT count(*) FROM tasks WHERE organization_id = :organizationId AND status NOT IN ('COMPLETED','CANCELLED')),
              (SELECT count(*) FROM leave_requests WHERE organization_id = :organizationId AND status = 'PENDING'),
              (SELECT count(*) FROM eod_reports WHERE organization_id = :organizationId AND status = 'SUBMITTED'),
              (SELECT count(*) FROM attendance_records WHERE organization_id = :organizationId AND attendance_date = CURRENT_DATE AND status IN ('PRESENT','LATE','HALF_DAY')),
              (SELECT count(DISTINCT employee_id) FROM leave_requests WHERE organization_id = :organizationId AND status = 'APPROVED' AND start_date <= CURRENT_DATE AND end_date >= CURRENT_DATE),
              (SELECT count(*) FROM tasks WHERE organization_id = :organizationId AND due_date < CURRENT_DATE AND status NOT IN ('COMPLETED','CANCELLED'))
            """, nativeQuery = true)
    Object[] totals(@Param("organizationId") UUID organizationId);

    @Query(value = "SELECT status,count(*) FROM tasks WHERE organization_id=:organizationId GROUP BY status ORDER BY status", nativeQuery = true)
    List<Object[]> taskStatuses(@Param("organizationId") UUID organizationId);

    @Query(value = """
            SELECT attendance_date,
                   sum(CASE WHEN status IN ('PRESENT','LATE','HALF_DAY') THEN 1 ELSE 0 END),
                   sum(CASE WHEN status = 'ABSENT' THEN 1 ELSE 0 END)
            FROM attendance_records
            WHERE organization_id = :organizationId
              AND attendance_date BETWEEN CURRENT_DATE - 6 AND CURRENT_DATE
            GROUP BY attendance_date ORDER BY attendance_date
            """, nativeQuery = true)
    List<Object[]> weeklyAttendance(@Param("organizationId") UUID organizationId);

    @Query(value = """
            SELECT d.name, count(DISTINCT e.id),
                   count(DISTINCT CASE WHEN a.status IN ('PRESENT','LATE','HALF_DAY') THEN e.id END)
            FROM departments d
            LEFT JOIN teams team ON team.department_id = d.id AND team.organization_id = d.organization_id
            LEFT JOIN team_members tm ON tm.team_id = team.id AND tm.organization_id = d.organization_id
            LEFT JOIN employees e ON e.id = tm.employee_id AND e.organization_id = d.organization_id
            LEFT JOIN attendance_records a ON a.employee_id = e.id AND a.organization_id = d.organization_id
                 AND a.attendance_date = CURRENT_DATE
            WHERE d.organization_id = :organizationId
            GROUP BY d.id, d.name ORDER BY d.name
            """, nativeQuery = true)
    List<Object[]> departmentAttendance(@Param("organizationId") UUID organizationId);

    @Query(value = """
            SELECT id, name, status, progress FROM projects
            WHERE organization_id = :organizationId AND status = 'ACTIVE'
            ORDER BY name
            """, nativeQuery = true)
    List<Object[]> projectProgress(@Param("organizationId") UUID organizationId);

    @Query(value = """
            SELECT e.id, u.first_name, u.last_name, e.date_of_birth
            FROM employees e JOIN users u ON u.id = e.user_id
            WHERE e.organization_id = :organizationId AND e.date_of_birth IS NOT NULL
              AND (
                (CURRENT_DATE + 30 < date_trunc('year', CURRENT_DATE) + INTERVAL '1 year'
                 AND to_char(e.date_of_birth, 'MM-DD') BETWEEN to_char(CURRENT_DATE, 'MM-DD')
                     AND to_char(CURRENT_DATE + 30, 'MM-DD'))
                OR
                (CURRENT_DATE + 30 >= date_trunc('year', CURRENT_DATE) + INTERVAL '1 year'
                 AND (to_char(e.date_of_birth, 'MM-DD') >= to_char(CURRENT_DATE, 'MM-DD')
                      OR to_char(e.date_of_birth, 'MM-DD') <= to_char(CURRENT_DATE + 30, 'MM-DD')))
              )
            ORDER BY CASE WHEN to_char(e.date_of_birth, 'MM-DD') >= to_char(CURRENT_DATE, 'MM-DD') THEN 0 ELSE 1 END,
                     to_char(e.date_of_birth, 'MM-DD')
            LIMIT 10
            """, nativeQuery = true)
    List<Object[]> upcomingBirthdays(@Param("organizationId") UUID organizationId);

    @Query(value = """
            SELECT (SELECT count(*) FROM organizations),
                   (SELECT count(*) FROM organizations WHERE status = 'ACTIVE'),
                   (SELECT count(*) FROM users)
            """, nativeQuery = true)
    Object[] platformTotals();
}
