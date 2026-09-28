package com.codenzic.workspace.dashboard.presentation.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import com.codenzic.workspace.attendance.presentation.dto.AttendanceResponse;
import com.codenzic.workspace.audit.presentation.dto.AuditLogResponse;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Schema(description = "Dashboard Dto payload.")
public record DashboardDto(
        long employees,
        long activeProjects,
        long openTasks,
        long pendingLeave,
        long pendingEod,
        Map<String, Long> tasksByStatus,
        long presentToday,
        long employeesOnLeave,
        long overdueTasks,
        List<DepartmentAttendance> departmentAttendance,
        List<WeeklyAttendance> weeklyAttendance,
        List<ProjectProgress> projectProgress,
        List<UpcomingBirthday> upcomingBirthdays,
        AttendanceResponse currentCheckIn,
        List<AuditLogResponse> recentActivity,
        long unreadNotifications,
        boolean platformDashboard,
        long organizations,
        long activeOrganizations,
        long users
) {
@Schema(description = "Department Attendance payload.")
    public record DepartmentAttendance(String department, long employees, long present) {
    }

@Schema(description = "Weekly Attendance payload.")
    public record WeeklyAttendance(LocalDate date, long present, long absent) {
    }

@Schema(description = "Project Progress payload.")
    public record ProjectProgress(UUID id, String name, String status, int progress) {
    }

@Schema(description = "Upcoming Birthday payload.")
    public record UpcomingBirthday(UUID employeeId, String firstName, String lastName, LocalDate dateOfBirth) {
    }
}
