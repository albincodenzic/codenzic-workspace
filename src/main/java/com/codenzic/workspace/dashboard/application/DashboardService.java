package com.codenzic.workspace.dashboard.application;

import com.codenzic.workspace.attendance.domain.Attendance;
import com.codenzic.workspace.attendance.infrastructure.AttendanceRepository;
import com.codenzic.workspace.attendance.presentation.dto.AttendanceResponse;
import com.codenzic.workspace.audit.application.AuditLogService;
import com.codenzic.workspace.audit.presentation.dto.AuditLogResponse;
import com.codenzic.workspace.common.security.CurrentUser;
import com.codenzic.workspace.communication.infrastructure.NotificationRepository;
import com.codenzic.workspace.dashboard.infrastructure.DashboardRepository;
import com.codenzic.workspace.dashboard.presentation.dto.DashboardDto;
import com.codenzic.workspace.employee.domain.Employee;
import com.codenzic.workspace.employee.infrastructure.EmployeeRepository;
import com.codenzic.workspace.identity.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DashboardService {
    private final DashboardRepository repository;
    private final EmployeeRepository employees;
    private final AttendanceRepository attendance;
    private final NotificationRepository notifications;
    private final AuditLogService auditLogs;

    @Transactional(readOnly = true)
    public DashboardDto get() {
        User user = CurrentUser.required();
        if (user.isPlatformUser()) {
            Object[] totals = repository.platformTotals();
            return new DashboardDto(0, 0, 0, 0, 0, Map.of(), 0, 0, 0, List.of(), List.of(),
                    List.of(), List.of(), null, auditLogs.list(5, null), unreadCount(user.getId()), true,
                    number(totals[0]), number(totals[1]), number(totals[2]));
        }

        UUID organizationId = CurrentUser.requiredOrganizationId();
        Object[] totals = repository.totals(organizationId);
        Map<String, Long> taskStatuses = new LinkedHashMap<>();
        repository.taskStatuses(organizationId).forEach(row ->
                taskStatuses.put(String.valueOf(row[0]), number(row[1])));

        List<DashboardDto.DepartmentAttendance> departmentAttendance = repository.departmentAttendance(organizationId)
                .stream().map(row -> new DashboardDto.DepartmentAttendance(
                        String.valueOf(row[0]), number(row[1]), number(row[2]))).toList();
        List<DashboardDto.WeeklyAttendance> weeklyAttendance = repository.weeklyAttendance(organizationId)
                .stream().map(row -> new DashboardDto.WeeklyAttendance(
                        toDate(row[0]), number(row[1]), number(row[2]))).toList();
        List<DashboardDto.ProjectProgress> projects = repository.projectProgress(organizationId)
                .stream().map(row -> new DashboardDto.ProjectProgress((UUID) row[0], String.valueOf(row[1]),
                        String.valueOf(row[2]), ((Number) row[3]).intValue())).toList();
        List<DashboardDto.UpcomingBirthday> birthdays = repository.upcomingBirthdays(organizationId)
                .stream().map(row -> new DashboardDto.UpcomingBirthday((UUID) row[0],
                        String.valueOf(row[1]), String.valueOf(row[2]), toDate(row[3]))).toList();

        AttendanceResponse currentCheckIn = employees.findByUserIdAndOrganizationId(user.getId(), organizationId)
                .flatMap(employee -> attendance.findByEmployeeIdAndAttendanceDate(employee.getId(), LocalDate.now()))
                .map(this::toResponse).orElse(null);
        List<AuditLogResponse> activity = auditLogs.list(5, organizationId);
        return new DashboardDto(number(totals[0]), number(totals[1]), number(totals[2]), number(totals[3]),
                number(totals[4]), taskStatuses, number(totals[5]), number(totals[6]), number(totals[7]),
                departmentAttendance, weeklyAttendance, projects, birthdays, currentCheckIn, activity,
                unreadCount(user.getId()), false, 0, 0, 0);
    }

    private long unreadCount(UUID userId) {
        return notifications.countByUserIdAndReadAtIsNull(userId);
    }

    private AttendanceResponse toResponse(Attendance record) {
        return new AttendanceResponse(record.getId(), record.getOrganizationId(), record.getEmployeeId(),
                record.getAttendanceDate(), record.getStatus(), record.getCheckIn(), record.getCheckOut(),
                record.getNotes());
    }

    private long number(Object value) {
        return ((Number) value).longValue();
    }

    private LocalDate toDate(Object value) {
        if (value instanceof Date date) return date.toLocalDate();
        if (value instanceof LocalDate date) return date;
        return LocalDate.parse(String.valueOf(value));
    }
}
