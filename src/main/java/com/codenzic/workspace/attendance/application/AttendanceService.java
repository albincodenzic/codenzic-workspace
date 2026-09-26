package com.codenzic.workspace.attendance.application;

import com.codenzic.workspace.audit.application.AuditLogService;
import com.codenzic.workspace.attendance.domain.Attendance;
import com.codenzic.workspace.attendance.infrastructure.AttendanceRepository;
import com.codenzic.workspace.attendance.presentation.dto.*;
import com.codenzic.workspace.common.api.PageResponse;
import com.codenzic.workspace.common.exception.BusinessException;
import com.codenzic.workspace.common.security.CurrentUser;
import com.codenzic.workspace.employee.domain.Employee;
import com.codenzic.workspace.employee.infrastructure.EmployeeRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AttendanceService {
    private final AttendanceRepository repository;
    private final EmployeeRepository employees;
    private final AuditLogService audit;

    @Transactional
    public AttendanceResponse create(AttendanceRequest request) {
        UUID organizationId = scope();
        UUID employeeId = employee(request.employeeId(), organizationId);
        if (repository.existsByEmployeeIdAndAttendanceDate(employeeId, request.attendanceDate())) {
            throw new BusinessException("ATTENDANCE_EXISTS", "Attendance already exists", HttpStatus.CONFLICT);
        }
        validateTimes(request.checkIn(), request.checkOut());
        Attendance record = repository.save(new Attendance(organizationId, employeeId, request.attendanceDate(), request.status(),
                request.checkIn(), request.checkOut(), request.notes()));
        audit.record(organizationId, CurrentUser.id(), "CREATE", "ATTENDANCE", record.getId(), "{}");
        return to(record);
    }

    @Transactional(readOnly = true)
    public List<AttendanceResponse> list() {
        return repository.findAllByOrganizationIdOrderByAttendanceDateDesc(scope()).stream().map(this::to).toList();
    }

    @Transactional(readOnly = true)
    public AttendanceResponse get(UUID id) {
        return to(find(id));
    }

    @Transactional(readOnly = true)
    public AttendanceResponse todayForMe() {
        UUID organizationId = scope();
        UUID employeeId = employee(null, organizationId);
        return to(repository.findByEmployeeIdAndAttendanceDate(employeeId, LocalDate.now())
                .orElseThrow(() -> new BusinessException("ATTENDANCE_NOT_FOUND", "No attendance record for today", HttpStatus.NOT_FOUND)));
    }

    @Transactional(readOnly = true)
    public List<AttendanceResponse> myHistory(LocalDate from, LocalDate to) {
        validateRange(from, to);
        UUID organizationId = scope();
        UUID employeeId = employee(null, organizationId);
        return repository.findAllByOrganizationIdAndEmployeeIdAndAttendanceDateBetweenOrderByAttendanceDateDesc(
                organizationId, employeeId, from, to).stream().map(this::to).toList();
    }

    @Transactional(readOnly = true)
    public List<AttendanceResponse> calendar(LocalDate from, LocalDate to) {
        validateRange(from, to);
        return repository.findAllByOrganizationIdAndAttendanceDateBetweenOrderByAttendanceDateAsc(scope(), from, to)
                .stream().map(this::to).toList();
    }

    @Transactional(readOnly = true)
    public List<AttendanceResponse> myCalendar(LocalDate from, LocalDate to) {
        return myHistory(from, to);
    }

    @Transactional(readOnly = true)
    public PageResponse<AttendanceResponse> monitor(
            LocalDate date,
            UUID employeeId,
            UUID departmentId,
            UUID teamId,
            String status,
            Pageable pageable
    ) {
        UUID organizationId = scope();
        if (employeeId != null) {
            employee(employeeId, organizationId);
        }
        String statusFilter = status == null || status.isBlank() ? null : status.trim().toUpperCase(Locale.ROOT);
        Page<AttendanceResponse> results = repository.monitor(organizationId, date, employeeId, departmentId,
                teamId, statusFilter, pageable).map(this::to);
        return PageResponse.from(results);
    }

    @Transactional
    public AttendancePunchResponse checkIn() {
        UUID organizationId = scope();
        UUID employeeId = employee(null, organizationId);
        LocalDate date = LocalDate.now();
        Instant now = Instant.now();
        Attendance attendance = repository.findByEmployeeIdAndAttendanceDate(employeeId, date)
                .orElseGet(() -> new Attendance(organizationId, employeeId, date, "PRESENT", null, null, null));
        try {
            attendance.checkIn(now);
        } catch (IllegalStateException exception) {
            throw new BusinessException("INVALID_ATTENDANCE_TRANSITION", exception.getMessage(), HttpStatus.CONFLICT);
        }
        try {
            Attendance saved = repository.saveAndFlush(attendance);
            audit.record(organizationId, CurrentUser.id(), "CHECK_IN", "ATTENDANCE", saved.getId(), "{}");
            return new AttendancePunchResponse(to(saved), now);
        } catch (DataIntegrityViolationException exception) {
            throw new BusinessException("ATTENDANCE_EXISTS", "Attendance is already recorded for today", HttpStatus.CONFLICT);
        }
    }

    @Transactional
    public AttendancePunchResponse checkOut() {
        UUID organizationId = scope();
        UUID employeeId = employee(null, organizationId);
        Attendance attendance = repository.findByEmployeeIdAndAttendanceDate(employeeId, LocalDate.now())
                .orElseThrow(() -> new BusinessException("ATTENDANCE_NOT_FOUND", "Check in before checking out", HttpStatus.CONFLICT));
        Instant now = Instant.now();
        try {
            attendance.checkOut(now);
        } catch (IllegalStateException exception) {
            throw new BusinessException("INVALID_ATTENDANCE_TRANSITION", exception.getMessage(), HttpStatus.CONFLICT);
        }
        Attendance saved = repository.save(attendance);
        audit.record(organizationId, CurrentUser.id(), "CHECK_OUT", "ATTENDANCE", saved.getId(), "{}");
        return new AttendancePunchResponse(to(saved), now);
    }

    private UUID scope() {
        return CurrentUser.requiredOrganizationId();
    }

    private UUID employee(UUID requestedId, UUID organizationId) {
        if (requestedId != null) {
            employees.findByIdAndOrganizationId(requestedId, organizationId)
                    .orElseThrow(() -> new BusinessException("EMPLOYEE_NOT_FOUND", "Employee not found", HttpStatus.NOT_FOUND));
            return requestedId;
        }
        return employees.findByUserIdAndOrganizationId(CurrentUser.id(), organizationId)
                .map(Employee::getId)
                .orElseThrow(() -> new BusinessException("EMPLOYEE_NOT_FOUND", "Current user is not an employee", HttpStatus.FORBIDDEN));
    }

    private Attendance find(UUID id) {
        return repository.findByIdAndOrganizationId(id, scope())
                .orElseThrow(() -> new BusinessException("ATTENDANCE_NOT_FOUND", "Attendance not found", HttpStatus.NOT_FOUND));
    }

    private void validateTimes(Instant checkIn, Instant checkOut) {
        if (checkIn != null && checkOut != null && checkOut.isBefore(checkIn)) {
            throw new BusinessException("INVALID_TIME", "Check-out must not precede check-in", HttpStatus.BAD_REQUEST);
        }
    }

    private void validateRange(LocalDate from, LocalDate to) {
        if (from == null || to == null || to.isBefore(from)) {
            throw new BusinessException("INVALID_DATE_RANGE", "Both dates are required and the end must not precede the start", HttpStatus.BAD_REQUEST);
        }
    }

    private AttendanceResponse to(Attendance attendance) {
        return new AttendanceResponse(attendance.getId(), attendance.getOrganizationId(), attendance.getEmployeeId(),
                attendance.getAttendanceDate(), attendance.getStatus(), attendance.getCheckIn(), attendance.getCheckOut(), attendance.getNotes());
    }
}
