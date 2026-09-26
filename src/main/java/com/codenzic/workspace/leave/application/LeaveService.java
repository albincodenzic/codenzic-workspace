package com.codenzic.workspace.leave.application;

import com.codenzic.workspace.audit.application.AuditLogService;
import com.codenzic.workspace.common.exception.BusinessException;
import com.codenzic.workspace.common.security.CurrentUser;
import com.codenzic.workspace.communication.application.NotificationService;
import com.codenzic.workspace.employee.domain.Employee;
import com.codenzic.workspace.employee.infrastructure.EmployeeRepository;
import com.codenzic.workspace.leave.domain.LeaveBalance;
import com.codenzic.workspace.leave.domain.LeaveRequest;
import com.codenzic.workspace.leave.domain.LeaveType;
import com.codenzic.workspace.leave.infrastructure.LeaveBalanceRepository;
import com.codenzic.workspace.leave.infrastructure.LeaveRequestRepository;
import com.codenzic.workspace.leave.infrastructure.LeaveTypeRepository;
import com.codenzic.workspace.leave.presentation.dto.*;
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
public class LeaveService {
    private final LeaveRequestRepository requests;
    private final LeaveTypeRepository types;
    private final LeaveBalanceRepository balances;
    private final EmployeeRepository employees;
    private final AuditLogService audit;
    private final NotificationService notifications;

    @Transactional
    public LeaveResponse create(LeaveRequestDto request) {
        UUID organizationId = scope();
        UUID employeeId = employee(null, organizationId);
        validateRequestedEmployee(request.employeeId(), employeeId);
        validateDates(request.startDate(), request.endDate());
        String typeCode = request.leaveType().trim().toUpperCase(Locale.ROOT);
        LeaveType type = types.findByCodeIgnoreCaseAndOrganizationId(typeCode, organizationId)
                .filter(LeaveType::isActive)
                .orElseThrow(() -> new BusinessException("LEAVE_TYPE_NOT_FOUND", "Active leave type not found", HttpStatus.BAD_REQUEST));
        typeCode = type.getCode();
        List<LeaveRequest> overlaps = requests.findAllByOrganizationIdAndEmployeeIdAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
                organizationId, employeeId, request.endDate(), request.startDate());
        if (overlaps.stream().anyMatch(overlap -> !"REJECTED".equals(overlap.getStatus()))) {
            throw new BusinessException("LEAVE_OVERLAP", "Leave dates overlap an existing request", HttpStatus.CONFLICT);
        }
        LeaveRequest saved = requests.save(new LeaveRequest(organizationId, employeeId, typeCode,
                request.startDate(), request.endDate(), request.reason()));
        audit.record(organizationId, CurrentUser.id(), "CREATE", "LEAVE", saved.getId(), "{}");
        return to(saved);
    }

    @Transactional(readOnly = true)
    public List<LeaveResponse> list() {
        return requests.findAllByOrganizationIdOrderByStartDateDesc(scope()).stream().map(this::to).toList();
    }

    @Transactional(readOnly = true)
    public List<LeaveResponse> list(String status) {
        UUID organizationId = scope();
        if (status == null || status.isBlank()) {
            return requests.findAllByOrganizationIdOrderByStartDateDesc(organizationId).stream().map(this::to).toList();
        }
        return requests.findAllByOrganizationIdAndStatusOrderByStartDateDesc(organizationId,
                status.trim().toUpperCase(Locale.ROOT)).stream().map(this::to).toList();
    }

    @Transactional(readOnly = true)
    public List<LeaveResponse> myRequests() {
        UUID organizationId = scope();
        return requests.findAllByOrganizationIdAndEmployeeIdOrderByStartDateDesc(organizationId, employee(null, organizationId))
                .stream().map(this::to).toList();
    }

    @Transactional(readOnly = true)
    public LeaveResponse get(UUID id) {
        return to(findRequest(id));
    }

    @Transactional
    public LeaveResponse review(UUID id, LeaveReviewRequest review) {
        if (!CurrentUser.hasPermission("LEAVE_APPROVE")) {
            throw new BusinessException("ACCESS_DENIED", "You cannot review leave requests", HttpStatus.FORBIDDEN);
        }
        LeaveRequest request = findRequest(id);
        if (employees.findByUserIdAndOrganizationId(CurrentUser.id(), request.getOrganizationId())
                .map(Employee::getId).filter(request.getEmployeeId()::equals).isPresent()) {
            throw new BusinessException("SELF_APPROVAL_FORBIDDEN", "Employees cannot review their own leave requests", HttpStatus.FORBIDDEN);
        }
        try {
            request.transition(review.status(), CurrentUser.id(), review.comment());
        } catch (IllegalStateException exception) {
            throw new BusinessException("INVALID_TRANSITION", exception.getMessage(), HttpStatus.CONFLICT);
        }
        LeaveRequest saved = requests.save(request);
        audit.record(saved.getOrganizationId(), CurrentUser.id(), "REVIEW_" + review.status(), "LEAVE", saved.getId(),
                "{\"status\":\"" + review.status() + "\"}");
        Employee requester = employees.findByIdAndOrganizationId(saved.getEmployeeId(), saved.getOrganizationId())
                .orElseThrow(() -> new BusinessException("EMPLOYEE_NOT_FOUND", "Leave requester not found", HttpStatus.NOT_FOUND));
        notifications.notifyUser(saved.getOrganizationId(), requester.getUserId(), "LEAVE_" + review.status(),
                "Leave request " + review.status().toLowerCase(Locale.ROOT),
                "Your leave request has been " + review.status().toLowerCase(Locale.ROOT) + ".");
        return to(saved);
    }

    @Transactional
    public LeaveResponse update(UUID id, LeaveRequestDto update) {
        LeaveRequest request = findRequest(id);
        requireRequestOwner(request);
        validateRequestedEmployee(update.employeeId(), request.getEmployeeId());
        validateDates(update.startDate(), update.endDate());
        LeaveType type = types.findByCodeIgnoreCaseAndOrganizationId(
                update.leaveType().trim().toUpperCase(Locale.ROOT), request.getOrganizationId())
                .filter(LeaveType::isActive)
                .orElseThrow(() -> new BusinessException("LEAVE_TYPE_NOT_FOUND", "Active leave type not found", HttpStatus.BAD_REQUEST));
        boolean overlaps = requests.findAllByOrganizationIdAndEmployeeIdAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
                        request.getOrganizationId(), request.getEmployeeId(), update.endDate(), update.startDate()).stream()
                .anyMatch(existing -> !existing.getId().equals(id) && !"REJECTED".equals(existing.getStatus()));
        if (overlaps) {
            throw new BusinessException("LEAVE_OVERLAP", "Leave dates overlap an existing request", HttpStatus.CONFLICT);
        }
        try {
            request.update(update.startDate(), update.endDate(), update.reason());
            request.changeLeaveType(type.getCode());
        } catch (IllegalStateException exception) {
            throw new BusinessException("INVALID_TRANSITION", exception.getMessage(), HttpStatus.CONFLICT);
        }
        LeaveRequest saved = requests.save(request);
        audit.record(saved.getOrganizationId(), CurrentUser.id(), "UPDATE", "LEAVE", saved.getId(), "{}");
        return to(saved);
    }

    @Transactional
    public LeaveResponse cancel(UUID id) {
        LeaveRequest request = findRequest(id);
        requireRequestOwner(request);
        try {
            request.transition("CANCELLED", CurrentUser.id(), "Cancelled by employee");
        } catch (IllegalStateException exception) {
            throw new BusinessException("INVALID_TRANSITION", exception.getMessage(), HttpStatus.CONFLICT);
        }
        LeaveRequest saved = requests.save(request);
        audit.record(saved.getOrganizationId(), CurrentUser.id(), "CANCEL", "LEAVE", saved.getId(), "{}");
        return to(saved);
    }

    @Transactional(readOnly = true)
    public List<LeaveResponse> calendar(LocalDate from, LocalDate to) {
        validateDates(from, to);
        return requests.findAllByOrganizationIdAndStartDateLessThanEqualAndEndDateGreaterThanEqualOrderByStartDateAsc(
                        scope(), to, from).stream()
                .filter(request -> "APPROVED".equals(request.getStatus())).map(this::to).toList();
    }

    @Transactional(readOnly = true)
    public List<LeaveResponse> monitor(String status) {
        return list(status);
    }

    @Transactional(readOnly = true)
    public List<LeaveTypeResponse> types() {
        return types.findAllByOrganizationIdOrderByName(scope()).stream().map(this::to).toList();
    }

    @Transactional
    public LeaveTypeResponse createType(LeaveTypeRequest request) {
        UUID organizationId = scope();
        String code = request.code().trim().toUpperCase(Locale.ROOT);
        if (types.findByCodeIgnoreCaseAndOrganizationId(code, organizationId).isPresent()) {
            throw new BusinessException("LEAVE_TYPE_EXISTS", "Leave type code already exists", HttpStatus.CONFLICT);
        }
        return to(types.save(new LeaveType(organizationId, code, request.name().trim(), request.annualAllowance())));
    }

    @Transactional
    public LeaveTypeResponse updateType(UUID id, LeaveTypeRequest request) {
        UUID organizationId = scope();
        String code = request.code().trim().toUpperCase(Locale.ROOT);
        LeaveType type = types.findByIdAndOrganizationId(id, organizationId)
                .orElseThrow(() -> new BusinessException("LEAVE_TYPE_NOT_FOUND", "Leave type not found", HttpStatus.NOT_FOUND));
        types.findByCodeIgnoreCaseAndOrganizationId(code, organizationId)
                .filter(existing -> !existing.getId().equals(id))
                .ifPresent(existing -> {
                    throw new BusinessException("LEAVE_TYPE_EXISTS", "Leave type code already exists", HttpStatus.CONFLICT);
                });
        type.update(code, request.name().trim(), request.annualAllowance(), request.active());
        return to(types.save(type));
    }

    @Transactional(readOnly = true)
    public List<LeaveBalanceResponse> myBalances(int year) {
        UUID organizationId = scope();
        return balanceFor(employee(null, organizationId), year, organizationId);
    }

    @Transactional(readOnly = true)
    public List<LeaveBalanceResponse> balances(UUID employeeId, int year) {
        UUID organizationId = scope();
        requireEmployee(employeeId, organizationId);
        return balanceFor(employeeId, year, organizationId);
    }

    @Transactional
    public LeaveBalanceResponse setBalance(LeaveBalanceRequest request) {
        UUID organizationId = scope();
        requireEmployee(request.employeeId(), organizationId);
        LeaveType type = types.findByIdAndOrganizationId(request.leaveTypeId(), organizationId)
                .orElseThrow(() -> new BusinessException("LEAVE_TYPE_NOT_FOUND", "Leave type not found", HttpStatus.NOT_FOUND));
        LeaveBalance balance = balances.findByOrganizationIdAndEmployeeIdAndLeaveTypeIdAndBalanceYear(
                        organizationId, request.employeeId(), request.leaveTypeId(), request.year())
                .orElseGet(() -> new LeaveBalance(organizationId, request.employeeId(), request.leaveTypeId(),
                        request.year(), request.allowanceDays()));
        balance.updateAllowance(request.allowanceDays());
        balances.save(balance);
        return balanceResponse(balance.getEmployeeId(), type, balance.getBalanceYear(), balance.getAllowanceDays(), organizationId);
    }

    private List<LeaveBalanceResponse> balanceFor(UUID employeeId, int year, UUID organizationId) {
        return types.findAllByOrganizationIdOrderByName(organizationId).stream()
                .filter(LeaveType::isActive)
                .map(type -> {
                    LeaveBalance override = balances.findByOrganizationIdAndEmployeeIdAndLeaveTypeIdAndBalanceYear(
                            organizationId, employeeId, type.getId(), year).orElse(null);
                    int allowance = override == null ? type.getAnnualAllowance() : override.getAllowanceDays();
                    return balanceResponse(employeeId, type, year, allowance, organizationId);
                }).toList();
    }

    private LeaveBalanceResponse balanceResponse(UUID employeeId, LeaveType type, int year, int allowance, UUID organizationId) {
        LocalDate yearStart = LocalDate.of(year, 1, 1);
        LocalDate yearEnd = LocalDate.of(year, 12, 31);
        int used = requests.findAllByOrganizationIdAndEmployeeIdAndLeaveTypeAndStatusAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
                        organizationId, employeeId, type.getCode(), "APPROVED", yearEnd, yearStart).stream()
                .mapToInt(request -> daysWithin(request, yearStart, yearEnd)).sum();
        return new LeaveBalanceResponse(employeeId, type.getId(), type.getCode(), type.getName(), year, allowance, used,
                Math.max(0, allowance - used));
    }

    private int daysWithin(LeaveRequest request, LocalDate start, LocalDate end) {
        LocalDate overlapStart = request.getStartDate().isAfter(start) ? request.getStartDate() : start;
        LocalDate overlapEnd = request.getEndDate().isBefore(end) ? request.getEndDate() : end;
        return (int) (overlapEnd.toEpochDay() - overlapStart.toEpochDay() + 1);
    }

    private UUID scope() {
        return CurrentUser.requiredOrganizationId();
    }

    private UUID employee(UUID id, UUID organizationId) {
        if (id != null) {
            requireEmployee(id, organizationId);
            return id;
        }
        return employees.findAllByOrganizationId(organizationId).stream()
                .filter(candidate -> candidate.getUserId().equals(CurrentUser.id()))
                .map(Employee::getId).findFirst()
                .orElseThrow(() -> new BusinessException("EMPLOYEE_NOT_FOUND", "Current user is not an employee", HttpStatus.FORBIDDEN));
    }

    private void requireEmployee(UUID id, UUID organizationId) {
        if (!employees.existsByIdAndOrganizationId(id, organizationId)) {
            throw new BusinessException("EMPLOYEE_NOT_FOUND", "Employee not found in the current organization", HttpStatus.NOT_FOUND);
        }
    }

    private void validateRequestedEmployee(UUID requestedEmployeeId, UUID currentEmployeeId) {
        if (requestedEmployeeId != null && !requestedEmployeeId.equals(currentEmployeeId)) {
            throw new BusinessException("EMPLOYEE_SCOPE_FORBIDDEN",
                    "Leave requests can only be created or updated for the current employee", HttpStatus.FORBIDDEN);
        }
    }

    private LeaveRequest findRequest(UUID id) {
        LeaveRequest request = requests.findByIdAndOrganizationId(id, scope())
                .orElseThrow(() -> new BusinessException("LEAVE_NOT_FOUND", "Leave request not found", HttpStatus.NOT_FOUND));
        if (!CurrentUser.hasPermission("LEAVE_READ") && !CurrentUser.hasPermission("LEAVE_APPROVE")) {
            requireRequestOwner(request);
        }
        return request;
    }

    private void requireRequestOwner(LeaveRequest request) {
        UUID employeeId = employee(null, request.getOrganizationId());
        if (!employeeId.equals(request.getEmployeeId())) {
            throw new BusinessException("LEAVE_NOT_FOUND", "Leave request not found", HttpStatus.NOT_FOUND);
        }
    }

    private void validateDates(LocalDate start, LocalDate end) {
        if (start == null || end == null || end.isBefore(start)) {
            throw new BusinessException("INVALID_DATES", "End date must not precede start date", HttpStatus.BAD_REQUEST);
        }
    }

    private LeaveResponse to(LeaveRequest request) {
        return new LeaveResponse(request.getId(), request.getOrganizationId(), request.getEmployeeId(), request.getLeaveType(),
                request.getStartDate(), request.getEndDate(), request.getReason(), request.getStatus(), request.getReviewedBy(),
                request.getReviewedAt(), request.getReviewComment());
    }

    private LeaveTypeResponse to(LeaveType type) {
        return new LeaveTypeResponse(type.getId(), type.getOrganizationId(), type.getCode(), type.getName(),
                type.getAnnualAllowance(), type.isActive());
    }
}
