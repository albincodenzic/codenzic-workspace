ALTER TABLE organizations
    ADD COLUMN status VARCHAR(30) NOT NULL DEFAULT 'ACTIVE',
    ADD CONSTRAINT ck_organizations_status CHECK (status IN ('ACTIVE', 'SUSPENDED'));

CREATE TABLE refresh_tokens (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    token_hash VARCHAR(64) NOT NULL UNIQUE,
    expires_at TIMESTAMPTZ NOT NULL,
    revoked_at TIMESTAMPTZ,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_refresh_tokens_user_id ON refresh_tokens(user_id);
CREATE INDEX idx_refresh_tokens_expires_at ON refresh_tokens(expires_at);

INSERT INTO permissions (code, description) VALUES
    ('ORGANIZATION_READ', 'Read organizations'),
    ('ORGANIZATION_UPDATE', 'Update organizations'),
    ('ROLE_MANAGE', 'Manage roles and role assignments'),
    ('PERMISSION_MANAGE', 'Read and manage permission catalog'),
    ('EMPLOYEE_CREATE', 'Create employees'),
    ('EMPLOYEE_READ', 'Read employees'),
    ('EMPLOYEE_UPDATE', 'Update employees'),
    ('EMPLOYEE_DEACTIVATE', 'Deactivate employees'),
    ('ATTENDANCE_CHECK_IN', 'Check in for attendance'),
    ('ATTENDANCE_CHECK_OUT', 'Check out from attendance'),
    ('ATTENDANCE_VIEW_SELF', 'View personal attendance'),
    ('ATTENDANCE_VIEW_ORGANIZATION', 'View organization attendance'),
    ('LEAVE_APPLY', 'Apply for leave'),
    ('LEAVE_VIEW_SELF', 'View personal leave'),
    ('LEAVE_APPROVE', 'Approve leave requests'),
    ('EOD_CREATE', 'Create end-of-day reports'),
    ('EOD_VIEW_SELF', 'View personal end-of-day reports'),
    ('EOD_REVIEW', 'Review end-of-day reports'),
    ('PROJECT_CREATE', 'Create projects'),
    ('PROJECT_READ', 'Read projects'),
    ('PROJECT_UPDATE', 'Update projects'),
    ('TASK_CREATE', 'Create tasks'),
    ('TASK_READ', 'Read tasks'),
    ('TASK_ASSIGN', 'Assign tasks'),
    ('TASK_UPDATE', 'Update tasks'),
    ('DEPARTMENT_CREATE', 'Create departments'),
    ('DEPARTMENT_READ', 'Read departments'),
    ('DEPARTMENT_UPDATE', 'Update departments'),
    ('TEAM_CREATE', 'Create teams'),
    ('TEAM_READ', 'Read teams'),
    ('TEAM_UPDATE', 'Update teams'),
    ('DASHBOARD_VIEW', 'View organization dashboard'),
    ('REPORT_VIEW', 'View organization reports'),
    ('COMPANY_SETTINGS_READ', 'Read company settings'),
    ('COMPANY_SETTINGS_UPDATE', 'Update company settings'),
    ('AUDIT_LOG_READ', 'Read audit logs')
ON CONFLICT (code) DO NOTHING;

INSERT INTO roles (name, description, organization_id, platform_role)
SELECT 'ORGANIZATION_ADMIN', 'Organization administrator', id, FALSE
FROM organizations
ON CONFLICT (organization_id, name) DO NOTHING;

INSERT INTO role_permissions (role_id, permission_id)
SELECT role.id, permission.id
FROM roles role
CROSS JOIN permissions permission
WHERE role.name = 'ORGANIZATION_ADMIN'
  AND role.platform_role = FALSE
  AND permission.code IN (
      'ORGANIZATION_READ', 'ORGANIZATION_UPDATE', 'ROLE_MANAGE', 'PERMISSION_MANAGE',
      'EMPLOYEE_CREATE', 'EMPLOYEE_READ', 'EMPLOYEE_UPDATE', 'EMPLOYEE_DEACTIVATE',
      'ATTENDANCE_CHECK_IN', 'ATTENDANCE_CHECK_OUT', 'ATTENDANCE_VIEW_SELF', 'ATTENDANCE_VIEW_ORGANIZATION',
      'LEAVE_APPLY', 'LEAVE_VIEW_SELF', 'LEAVE_APPROVE',
      'EOD_CREATE', 'EOD_VIEW_SELF', 'EOD_REVIEW',
      'PROJECT_CREATE', 'PROJECT_READ', 'PROJECT_UPDATE',
      'TASK_CREATE', 'TASK_READ', 'TASK_ASSIGN', 'TASK_UPDATE',
      'DEPARTMENT_CREATE', 'DEPARTMENT_READ', 'DEPARTMENT_UPDATE',
      'TEAM_CREATE', 'TEAM_READ', 'TEAM_UPDATE',
      'DASHBOARD_VIEW', 'REPORT_VIEW', 'COMPANY_SETTINGS_READ', 'COMPANY_SETTINGS_UPDATE', 'AUDIT_LOG_READ'
  )
ON CONFLICT DO NOTHING;
