package com.codenzic.workspace.employee.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;
import java.time.LocalDate;

@Entity
@Table(name = "employees")
@Getter
@NoArgsConstructor
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private UUID organizationId;

    @Column(name = "user_id", nullable = false, unique = true)
    private UUID userId;

    @Column(length = 150)
    private String jobTitle;

    @Column(length = 40)
    private String phone;

    @Column(nullable = false)
    private boolean active = true;

    @Enumerated(EnumType.STRING)
    @Column(name = "employment_status", nullable = false, length = 20)
    private EmployeeStatus status = EmployeeStatus.ACTIVE;

    private LocalDate dateOfBirth;

    public Employee(UUID organizationId, UUID userId, String jobTitle, String phone) {
        this(organizationId, userId, jobTitle, phone, null);
    }

    public Employee(UUID organizationId, UUID userId, String jobTitle, String phone, LocalDate dateOfBirth) {
        this.organizationId = organizationId;
        this.userId = userId;
        this.jobTitle = jobTitle;
        this.phone = phone;
        this.dateOfBirth = dateOfBirth;
    }

    public void update(String jobTitle, String phone, LocalDate dateOfBirth) {
        this.jobTitle = jobTitle;
        this.phone = phone;
        this.dateOfBirth = dateOfBirth;
    }

    public void changeStatus(EmployeeStatus status) {
        this.status = status;
        this.active = status == EmployeeStatus.ACTIVE;
    }

    public void deactivate() {
        changeStatus(EmployeeStatus.INACTIVE);
    }
}