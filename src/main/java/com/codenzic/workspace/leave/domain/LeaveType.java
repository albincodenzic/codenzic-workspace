package com.codenzic.workspace.leave.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "leave_types", uniqueConstraints = @UniqueConstraint(columnNames = {"organization_id", "code"}))
@Getter
@NoArgsConstructor
public class LeaveType {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private UUID organizationId;

    @Column(nullable = false, length = 30)
    private String code;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false)
    private int annualAllowance;

    @Column(nullable = false)
    private boolean active = true;

    public LeaveType(UUID organizationId, String code, String name, int annualAllowance) {
        this.organizationId = organizationId;
        this.code = code;
        this.name = name;
        this.annualAllowance = annualAllowance;
    }

    public void update(String code, String name, int annualAllowance, boolean active) {
        this.code = code;
        this.name = name;
        this.annualAllowance = annualAllowance;
        this.active = active;
    }
}
