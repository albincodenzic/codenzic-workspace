ALTER TABLE employees
    ADD COLUMN employment_status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    ADD CONSTRAINT ck_employees_employment_status
        CHECK (employment_status IN ('ACTIVE', 'INACTIVE', 'SUSPENDED', 'TERMINATED'));

UPDATE employees
SET employment_status = CASE WHEN active THEN 'ACTIVE' ELSE 'INACTIVE' END;

CREATE INDEX idx_employees_org_status ON employees(organization_id, employment_status);

INSERT INTO permissions (code, description) VALUES
    ('PROJECT_READ_TEAM', 'Read projects assigned to the user''s teams'),
    ('PROJECT_READ_SELF', 'Read projects assigned to the current employee'),
    ('TASK_READ_TEAM', 'Read tasks assigned to the user''s teams'),
    ('TASK_READ_SELF', 'Read tasks assigned to the current employee'),
    ('TASK_UPDATE_TEAM', 'Update tasks assigned to the user''s teams'),
    ('TASK_UPDATE_SELF', 'Update tasks assigned to the current employee'),
    ('EOD_READ_TEAM', 'Read EOD reports for the user''s teams'),
    ('EOD_READ_SELF', 'Read EOD reports for the current employee'),
    ('EOD_REVIEW_TEAM', 'Review EOD reports for the user''s teams')
ON CONFLICT (code) DO NOTHING;

INSERT INTO roles (name, description, organization_id, platform_role)
SELECT role.name, role.description, organizations.id, FALSE
FROM organizations
CROSS JOIN (VALUES
    ('HR', 'Human resources'),
    ('TEAM_LEAD', 'Team lead'),
    ('DEVELOPER', 'Developer'),
    ('EMPLOYEE', 'Employee'),
    ('INTERN', 'Intern')
) AS role(name, description)
ON CONFLICT (organization_id, name) DO NOTHING;

INSERT INTO role_permissions (role_id, permission_id)
SELECT role.id, permission.id
FROM roles role
JOIN permissions permission ON permission.code IN (
    'EMPLOYEE_READ', 'DEPARTMENT_READ', 'TEAM_READ', 'ATTENDANCE_MONITOR',
    'LEAVE_READ', 'LEAVE_APPROVE', 'LEAVE_MONITOR', 'EOD_READ', 'EOD_MONITOR', 'EOD_REVIEW',
    'DASHBOARD_VIEW', 'REPORT_VIEW'
)
WHERE role.name = 'HR' AND role.platform_role = FALSE
ON CONFLICT DO NOTHING;

INSERT INTO role_permissions (role_id, permission_id)
SELECT role.id, permission.id
FROM roles role
JOIN permissions permission ON permission.code IN (
    'TEAM_READ', 'TEAM_MEMBER_MANAGE', 'PROJECT_READ_TEAM', 'TASK_READ_TEAM',
    'TASK_UPDATE_TEAM', 'TASK_ASSIGN', 'TASK_COMMENT', 'EOD_READ_TEAM', 'EOD_REVIEW_TEAM'
)
WHERE role.name = 'TEAM_LEAD' AND role.platform_role = FALSE
ON CONFLICT DO NOTHING;

INSERT INTO role_permissions (role_id, permission_id)
SELECT role.id, permission.id
FROM roles role
JOIN permissions permission ON permission.code IN (
    'PROJECT_READ_SELF', 'TASK_READ_SELF', 'TASK_UPDATE_SELF', 'TASK_COMMENT',
    'ATTENDANCE_CHECK_IN', 'ATTENDANCE_CHECK_OUT', 'ATTENDANCE_VIEW_SELF',
    'LEAVE_APPLY', 'LEAVE_CREATE', 'LEAVE_VIEW_SELF', 'EOD_CREATE', 'EOD_VIEW_SELF',
    'EOD_READ_SELF'
)
WHERE role.name IN ('DEVELOPER', 'EMPLOYEE', 'INTERN') AND role.platform_role = FALSE
ON CONFLICT DO NOTHING;
