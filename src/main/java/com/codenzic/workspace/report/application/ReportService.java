package com.codenzic.workspace.report.application;

import com.codenzic.workspace.common.exception.BusinessException;
import com.codenzic.workspace.common.security.CurrentUser;
import com.codenzic.workspace.report.infrastructure.ReportRepository;
import com.codenzic.workspace.report.presentation.dto.ReportDtos.Attendance;
import com.codenzic.workspace.report.presentation.dto.ReportDtos.Eod;
import com.codenzic.workspace.report.presentation.dto.ReportDtos.Employee;
import com.codenzic.workspace.report.presentation.dto.ReportDtos.Leave;
import com.codenzic.workspace.report.presentation.dto.ReportDtos.Project;
import com.codenzic.workspace.report.presentation.dto.ReportDtos.Response;
import com.codenzic.workspace.report.presentation.dto.ReportDtos.Task;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReportService {
    private final ReportRepository repository;

    @Transactional(readOnly = true)
    public Response get(LocalDate from, LocalDate to) {
        DateRange range = range(from, to);
        UUID organizationId = organizationId();
        return new Response(attendance(range, organizationId, null, null, null, null),
                leave(range, organizationId, null, null, null, null),
                employees(organizationId, null, null, null, null),
                projects(organizationId, null, null, null),
                tasks(organizationId, null, null, null, null, null),
                eod(range, organizationId, null, null, null));
    }

    @Transactional(readOnly = true)
    public List<Attendance> attendance(LocalDate from, LocalDate to, UUID employeeId, UUID departmentId, UUID teamId, String status) {
        UUID organizationId = organizationId();
        return attendance(range(from, to), organizationId, employeeId, departmentId, teamId, normalized(status));
    }

    @Transactional(readOnly = true)
    public List<Leave> leave(LocalDate from, LocalDate to, UUID employeeId, UUID departmentId, UUID teamId, String status) {
        UUID organizationId = organizationId();
        return leave(range(from, to), organizationId, employeeId, departmentId, teamId, normalized(status));
    }

    @Transactional(readOnly = true)
    public List<Employee> employees(UUID employeeId, UUID departmentId, UUID teamId, String status) {
        UUID organizationId = organizationId();
        return employees(organizationId, employeeId, departmentId, teamId, normalized(status));
    }

    @Transactional(readOnly = true)
    public List<Project> projects(UUID projectId, UUID employeeId, String status) {
        return projects(organizationId(), projectId, employeeId, normalized(status));
    }

    @Transactional(readOnly = true)
    public List<Task> tasks(UUID projectId, UUID employeeId, UUID departmentId, UUID teamId, String status, String priority) {
        return tasks(organizationId(), projectId, employeeId, departmentId, teamId,
                normalized(status), normalized(priority));
    }

    @Transactional(readOnly = true)
    public List<Eod> eod(LocalDate from, LocalDate to, UUID employeeId, UUID teamId, String status) {
        UUID organizationId = organizationId();
        return eod(range(from, to), organizationId, employeeId, teamId, normalized(status));
    }

    private List<Attendance> attendance(
            DateRange range, UUID organizationId, UUID employeeId, UUID departmentId, UUID teamId, String status
    ) {
        return repository.attendance(organizationId, range.from(), range.to(), employeeId, departmentId, teamId, status)
                .stream().map(row -> new Attendance(toDate(row[0]), String.valueOf(row[1]), count(row[2]))).toList();
    }

    private List<Leave> leave(
            DateRange range, UUID organizationId, UUID employeeId, UUID departmentId, UUID teamId, String status
    ) {
        return repository.leave(organizationId, range.from(), range.to(), employeeId, departmentId, teamId, status)
                .stream().map(row -> new Leave(String.valueOf(row[0]), String.valueOf(row[1]), count(row[2]))).toList();
    }

    private List<Employee> employees(UUID organizationId, UUID employeeId, UUID departmentId, UUID teamId, String status) {
        return repository.employees(organizationId, employeeId, departmentId, teamId, status)
                .stream().map(row -> new Employee(String.valueOf(row[0]), count(row[1]))).toList();
    }

    private List<Project> projects(UUID organizationId, UUID projectId, UUID employeeId, String status) {
        return repository.projects(organizationId, projectId, employeeId, status)
                .stream().map(row -> new Project((UUID) row[0], String.valueOf(row[1]), String.valueOf(row[2]),
                        ((Number) row[3]).intValue(), count(row[4]))).toList();
    }

    private List<Task> tasks(
            UUID organizationId, UUID projectId, UUID employeeId, UUID departmentId, UUID teamId,
            String status, String priority
    ) {
        return repository.tasks(organizationId, projectId, employeeId, departmentId, teamId, status, priority)
                .stream().map(row -> new Task(String.valueOf(row[0]), String.valueOf(row[1]), count(row[2]))).toList();
    }

    private List<Eod> eod(DateRange range, UUID organizationId, UUID employeeId, UUID teamId, String status) {
        return repository.eod(organizationId, range.from(), range.to(), employeeId, teamId, status)
                .stream().map(row -> new Eod(String.valueOf(row[0]), count(row[1]))).toList();
    }

    private UUID organizationId() {
        try {
            return CurrentUser.requiredOrganizationId();
        } catch (BusinessException exception) {
            throw new BusinessException("ORGANIZATION_REQUIRED", "Organization scope is required", HttpStatus.FORBIDDEN);
        }
    }

    private DateRange range(LocalDate from, LocalDate to) {
        LocalDate end = to == null ? LocalDate.now() : to;
        LocalDate start = from == null ? end.minusDays(30) : from;
        if (start.isAfter(end)) {
            throw new BusinessException("INVALID_DATE_RANGE", "from must not be after to", HttpStatus.BAD_REQUEST);
        }
        return new DateRange(start, end);
    }

    private String normalized(String value) {
        return value == null || value.isBlank() ? null : value.trim().toUpperCase(Locale.ROOT);
    }

    private long count(Object value) {
        return ((Number) value).longValue();
    }

    private LocalDate toDate(Object value) {
        return value instanceof java.sql.Date date ? date.toLocalDate()
                : value instanceof LocalDate date ? date : LocalDate.parse(String.valueOf(value));
    }

    private record DateRange(LocalDate from, LocalDate to) {
    }
}
