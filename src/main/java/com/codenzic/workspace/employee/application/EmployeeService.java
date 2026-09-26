package com.codenzic.workspace.employee.application;

import com.codenzic.workspace.audit.application.AuditLogService;
import com.codenzic.workspace.common.exception.BusinessException;
import com.codenzic.workspace.common.security.CurrentUser;
import com.codenzic.workspace.employee.domain.Employee;
import com.codenzic.workspace.employee.domain.EmployeeStatus;
import com.codenzic.workspace.employee.infrastructure.EmployeeRepository;
import com.codenzic.workspace.employee.presentation.dto.*;
import com.codenzic.workspace.identity.infrastructure.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import com.codenzic.workspace.common.api.PageResponse;

@Service
@RequiredArgsConstructor
public class EmployeeService {
    private final EmployeeRepository repository;
    private final UserRepository userRepository;
    private final AuditLogService audit;

    @Transactional
    public EmployeeResponse create(EmployeeRequest request) {
        UUID organizationId = CurrentUser.requiredOrganizationId();
        if (!userRepository.existsByIdAndOrganizationId(request.userId(), organizationId)) {
            throw new BusinessException("USER_ORGANIZATION_MISMATCH", "Employee user must belong to the current organization", HttpStatus.BAD_REQUEST);
        }
        if (repository.existsByUserIdAndOrganizationId(request.userId(), organizationId)) {
            throw new BusinessException("EMPLOYEE_ALREADY_EXISTS", "User already has an employee profile", HttpStatus.CONFLICT);
        }
        Employee employee = repository.save(new Employee(organizationId, request.userId(), request.jobTitle(), request.phone(),
                request.dateOfBirth()));
        audit.record(organizationId, CurrentUser.id(), "CREATE", "EMPLOYEE", employee.getId(), "{}");
        return to(employee);
    }

    @Transactional(readOnly = true)
    public PageResponse<EmployeeResponse> list(
            String search,
            EmployeeStatus status,
            UUID departmentId,
            UUID teamId,
            String role,
            Pageable pageable
    ) {
        UUID organizationId = CurrentUser.requiredOrganizationId();
        String term = search == null || search.isBlank() ? null : "%" + search.trim().toLowerCase() + "%";
        String roleFilter = role == null || role.isBlank() ? null : role.trim();
        Page<EmployeeResponse> employees = repository.search(
                organizationId, term, status, departmentId, teamId, roleFilter, pageable).map(this::to);
        return PageResponse.from(employees);
    }

    @Transactional(readOnly = true)
    public EmployeeResponse get(UUID id) {
        return to(find(id));
    }

    @Transactional
    public EmployeeResponse update(UUID id, EmployeeUpdateRequest request) {
        Employee employee = find(id);
        employee.update(request.jobTitle(), request.phone(), request.dateOfBirth());
        Employee updated = repository.save(employee);
        audit.record(updated.getOrganizationId(), CurrentUser.id(), "UPDATE", "EMPLOYEE", updated.getId(), "{}");
        return to(updated);
    }

    @Transactional
    public EmployeeResponse deactivate(UUID id) {
        Employee employee = find(id);
        employee.deactivate();
        Employee updated = repository.save(employee);
        audit.record(updated.getOrganizationId(), CurrentUser.id(), "STATUS_CHANGE", "EMPLOYEE", updated.getId(),
                "{\"status\":\"INACTIVE\"}");
        return to(updated);
    }

    @Transactional
    public EmployeeResponse changeStatus(UUID id, EmployeeStatus status) {
        Employee employee = find(id);
        employee.changeStatus(status);
        Employee updated = repository.save(employee);
        audit.record(updated.getOrganizationId(), CurrentUser.id(), "STATUS_CHANGE", "EMPLOYEE", updated.getId(),
                "{\"status\":\"" + status.name() + "\"}");
        return to(updated);
    }

    private Employee find(UUID id) {
        return repository.findByIdAndOrganizationId(id, CurrentUser.requiredOrganizationId())
                .orElseThrow(() -> new BusinessException("EMPLOYEE_NOT_FOUND", "Employee not found", HttpStatus.NOT_FOUND));
    }

    private EmployeeResponse to(Employee employee) {
        return new EmployeeResponse(employee.getId(), employee.getOrganizationId(), employee.getUserId(),
                employee.getJobTitle(), employee.getPhone(), employee.isActive(), employee.getStatus(), employee.getDateOfBirth());
    }
}
