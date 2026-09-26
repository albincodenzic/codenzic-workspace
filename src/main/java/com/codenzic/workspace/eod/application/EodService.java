package com.codenzic.workspace.eod.application;

import com.codenzic.workspace.audit.application.AuditLogService;
import com.codenzic.workspace.common.api.PageResponse;
import com.codenzic.workspace.common.exception.BusinessException;
import com.codenzic.workspace.common.security.CurrentUser;
import com.codenzic.workspace.communication.application.NotificationService;
import com.codenzic.workspace.eod.domain.EodReport;
import com.codenzic.workspace.eod.infrastructure.EodReportRepository;
import com.codenzic.workspace.eod.presentation.dto.EodRequest;
import com.codenzic.workspace.eod.presentation.dto.EodResponse;
import com.codenzic.workspace.employee.domain.Employee;
import com.codenzic.workspace.employee.infrastructure.EmployeeRepository;
import com.codenzic.workspace.team.infrastructure.TeamMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EodService {
    private final EodReportRepository repository;
    private final EmployeeRepository employees;
    private final TeamMemberRepository teamMembers;
    private final AuditLogService audit;
    private final NotificationService notifications;

    @Transactional
    public EodResponse create(EodRequest request) {
        UUID organizationId = organizationId();
        UUID employeeId = currentEmployee(organizationId).getId();
        validateRequestedEmployee(request.employeeId(), employeeId);
        if (repository.existsByEmployeeIdAndReportDate(employeeId, request.reportDate())) {
            throw new BusinessException("EOD_EXISTS", "EOD report already exists", HttpStatus.CONFLICT);
        }
        EodReport report = repository.save(new EodReport(organizationId, employeeId, request.reportDate(),
                request.summary(), request.blockers()));
        audit.record(organizationId, CurrentUser.id(), "CREATE", "EOD", report.getId(), "{}");
        return to(report);
    }

    @Transactional(readOnly = true)
    public PageResponse<EodResponse> list(
            UUID employeeId,
            UUID teamId,
            LocalDate from,
            LocalDate to,
            String status,
            Pageable pageable
    ) {
        UUID organizationId = organizationId();
        validateRange(from, to);
        List<UUID> scopedTeamIds = null;
        if (!CurrentUser.hasPermission("EOD_READ")) {
            Employee self = currentEmployee(organizationId);
            if (CurrentUser.hasPermission("EOD_READ_TEAM")) {
                scopedTeamIds = teamMembers.findAllByEmployeeIdAndOrganizationId(self.getId(), organizationId)
                        .stream().map(member -> member.getTeamId()).toList();
                if (teamId != null && !scopedTeamIds.contains(teamId)) {
                    throw new BusinessException("TEAM_NOT_FOUND", "Team not found in the current user's scope", HttpStatus.NOT_FOUND);
                }
                if (scopedTeamIds.isEmpty()) {
                    return PageResponse.from(Page.empty(pageable));
                }
            } else {
                employeeId = self.getId();
            }
        }
        String statusFilter = status == null || status.isBlank() ? null : status.trim().toUpperCase();
        List<UUID> visibleTeams = scopedTeamIds == null ? List.of(new UUID(0L, 0L))
                : teamId == null ? scopedTeamIds : List.of(teamId);
        Page<EodResponse> reports = repository.search(organizationId, employeeId, teamId, from, to,
                statusFilter, scopedTeamIds != null, visibleTeams, pageable).map(this::to);
        return PageResponse.from(reports);
    }

    @Transactional(readOnly = true)
    public EodResponse get(UUID id) {
        return to(find(id));
    }

    @Transactional
    public EodResponse update(UUID id, EodRequest request) {
        EodReport report = find(id);
        requireOwner(report);
        validateRequestedEmployee(request.employeeId(), report.getEmployeeId());
        report.updateDraft(request.summary(), request.blockers());
        EodReport updated = repository.save(report);
        audit.record(updated.getOrganizationId(), CurrentUser.id(), "UPDATE", "EOD", updated.getId(), "{}");
        return to(updated);
    }

    @Transactional
    public EodResponse submit(UUID id) {
        EodReport report = find(id);
        requireOwner(report);
        try {
            report.submit();
        } catch (IllegalStateException exception) {
            throw new BusinessException("INVALID_TRANSITION", exception.getMessage(), HttpStatus.CONFLICT);
        }
        EodReport submitted = repository.save(report);
        audit.record(submitted.getOrganizationId(), CurrentUser.id(), "SUBMIT", "EOD", submitted.getId(), "{}");
        return to(submitted);
    }

    @Transactional
    public EodResponse review(UUID id, String comment) {
        return reviewReport(id, "REVIEWED", comment);
    }

    @Transactional
    public EodResponse reject(UUID id, String comment) {
        return reviewReport(id, "REJECTED", comment);
    }

    private EodResponse reviewReport(UUID id, String status, String comment) {
        if (!CurrentUser.hasPermission("EOD_REVIEW") && !CurrentUser.hasPermission("EOD_REVIEW_TEAM")) {
            throw new BusinessException("ACCESS_DENIED", "You cannot review EOD reports", HttpStatus.FORBIDDEN);
        }
        EodReport report = find(id);
        try {
            report.review(status, CurrentUser.id(), comment);
        } catch (IllegalStateException exception) {
            throw new BusinessException("INVALID_TRANSITION", exception.getMessage(), HttpStatus.CONFLICT);
        }
        EodReport reviewed = repository.save(report);
        audit.record(reviewed.getOrganizationId(), CurrentUser.id(), status, "EOD", reviewed.getId(),
                "{\"status\":\"" + status + "\"}");
        Employee employee = employees.findByIdAndOrganizationId(reviewed.getEmployeeId(), reviewed.getOrganizationId())
                .orElseThrow(() -> new BusinessException("EMPLOYEE_NOT_FOUND", "EOD owner not found", HttpStatus.NOT_FOUND));
        notifications.notifyUser(reviewed.getOrganizationId(), employee.getUserId(), "EOD_" + status,
                "EOD report " + status.toLowerCase(), "Your EOD report has been " + status.toLowerCase() + ".");
        return to(reviewed);
    }

    private EodReport find(UUID id) {
        UUID organizationId = organizationId();
        EodReport report = repository.findByIdAndOrganizationId(id, organizationId)
                .orElseThrow(() -> new BusinessException("EOD_NOT_FOUND", "EOD report not found", HttpStatus.NOT_FOUND));
        if (CurrentUser.hasPermission("EOD_READ") || CurrentUser.hasPermission("EOD_REVIEW")) {
            return report;
        }
        UUID employeeId = currentEmployee(organizationId).getId();
        boolean ownReport = employeeId.equals(report.getEmployeeId());
        List<UUID> visibleTeamIds = teamMembers.findAllByEmployeeIdAndOrganizationId(employeeId, organizationId)
                .stream().map(member -> member.getTeamId()).toList();
        boolean teamReport = (CurrentUser.hasPermission("EOD_READ_TEAM")
                || CurrentUser.hasPermission("EOD_REVIEW_TEAM"))
                && teamMembers.findAllByEmployeeIdAndOrganizationId(report.getEmployeeId(), organizationId).stream()
                .anyMatch(member -> visibleTeamIds.contains(member.getTeamId()));
        if (ownReport || teamReport) {
            return report;
        }
        throw new BusinessException("EOD_NOT_FOUND", "EOD report not found", HttpStatus.NOT_FOUND);
    }

    private void requireOwner(EodReport report) {
        if (!report.getEmployeeId().equals(currentEmployee(report.getOrganizationId()).getId())) {
            throw new BusinessException("EOD_NOT_FOUND", "EOD report not found", HttpStatus.NOT_FOUND);
        }
    }

    private void validateRequestedEmployee(UUID requestedEmployeeId, UUID currentEmployeeId) {
        if (requestedEmployeeId != null && !requestedEmployeeId.equals(currentEmployeeId)) {
            throw new BusinessException("EMPLOYEE_SCOPE_FORBIDDEN",
                    "EOD reports can only be created or updated for the current employee", HttpStatus.FORBIDDEN);
        }
    }

    private Employee currentEmployee(UUID organizationId) {
        return employees.findByUserIdAndOrganizationId(CurrentUser.id(), organizationId)
                .orElseThrow(() -> new BusinessException("EMPLOYEE_NOT_FOUND", "Current user is not an employee", HttpStatus.FORBIDDEN));
    }

    private UUID organizationId() {
        return CurrentUser.requiredOrganizationId();
    }

    private void validateRange(LocalDate from, LocalDate to) {
        if (from != null && to != null && to.isBefore(from)) {
            throw new BusinessException("INVALID_DATE_RANGE", "End date must not precede start date", HttpStatus.BAD_REQUEST);
        }
    }

    private EodResponse to(EodReport report) {
        return new EodResponse(report.getId(), report.getOrganizationId(), report.getEmployeeId(), report.getReportDate(),
                report.getSummary(), report.getBlockers(), report.getStatus(), report.getSubmittedAt(),
                report.getReviewedBy(), report.getReviewedAt(), report.getReviewComment());
    }
}
