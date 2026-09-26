package com.codenzic.workspace.leave.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "leave_balances", uniqueConstraints = @UniqueConstraint(columnNames = {"employee_id", "leave_type_id", "balance_year"}))
@Getter
@NoArgsConstructor
public class LeaveBalance {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private UUID organizationId;

    @Column(nullable = false)
    private UUID employeeId;

    @Column(nullable = false)
    private UUID leaveTypeId;

    @Column(nullable = false)
    private int balanceYear;

    @Column(nullable = false)
    private int allowanceDays;

    public LeaveBalance(UUID organizationId, UUID employeeId, UUID leaveTypeId, int balanceYear, int allowanceDays) {
        this.organizationId = organizationId;
        this.employeeId = employeeId;
        this.leaveTypeId = leaveTypeId;
        this.balanceYear = balanceYear;
        this.allowanceDays = allowanceDays;
    }

    public void updateAllowance(int allowanceDays) {
        this.allowanceDays = allowanceDays;
    }
}
