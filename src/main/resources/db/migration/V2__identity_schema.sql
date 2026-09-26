CREATE TABLE users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    email VARCHAR(320) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    organization_id UUID,
    account_status VARCHAR(30) NOT NULL DEFAULT 'ACTIVE',
    platform_user BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    version BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT uk_users_email UNIQUE (email),
    CONSTRAINT ck_users_account_status CHECK (account_status IN ('ACTIVE', 'INACTIVE', 'SUSPENDED', 'LOCKED')),
    CONSTRAINT ck_users_platform_scope CHECK (platform_user = TRUE OR organization_id IS NOT NULL)
);

CREATE TABLE roles (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(80) NOT NULL,
    description VARCHAR(255),
    organization_id UUID,
    platform_role BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT uk_roles_scope_name UNIQUE (organization_id, name),
    CONSTRAINT ck_roles_platform_scope CHECK (platform_role = TRUE OR organization_id IS NOT NULL)
);

CREATE TABLE permissions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    code VARCHAR(120) NOT NULL UNIQUE,
    description VARCHAR(255) NOT NULL
);

CREATE TABLE user_roles (
    user_id UUID NOT NULL REFERENCES users(id),
    role_id UUID NOT NULL REFERENCES roles(id),
    PRIMARY KEY (user_id, role_id)
);

CREATE TABLE role_permissions (
    role_id UUID NOT NULL REFERENCES roles(id),
    permission_id UUID NOT NULL REFERENCES permissions(id),
    PRIMARY KEY (role_id, permission_id)
);

CREATE INDEX idx_users_organization_id ON users (organization_id);
CREATE INDEX idx_roles_organization_id ON roles (organization_id);
CREATE INDEX idx_user_roles_role_id ON user_roles (role_id);
CREATE INDEX idx_role_permissions_permission_id ON role_permissions (permission_id);

INSERT INTO permissions (code, description) VALUES
    ('AUTH_LOGIN', 'Authenticate into the platform'),
    ('AUTH_PASSWORD_CHANGE', 'Change the authenticated user password'),
    ('ROLE_MANAGE', 'Manage roles and role assignments'),
    ('PERMISSION_MANAGE', 'Manage permissions'),
    ('ORGANIZATION_CREATE', 'Create organizations'),
    ('ORGANIZATION_READ', 'Read organizations'),
    ('ORGANIZATION_UPDATE', 'Update organizations'),
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
    ('REPORT_VIEW', 'View reports')
ON CONFLICT (code) DO NOTHING;

INSERT INTO roles (name, description, platform_role) VALUES
    ('SUPER_ADMIN', 'Platform administrator', TRUE)
ON CONFLICT DO NOTHING;

INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r
JOIN permissions p ON p.code IN (
    'AUTH_LOGIN',
    'AUTH_PASSWORD_CHANGE',
    'ROLE_MANAGE',
    'PERMISSION_MANAGE',
    'ORGANIZATION_CREATE',
    'ORGANIZATION_READ',
    'ORGANIZATION_UPDATE',
    'EMPLOYEE_CREATE',
    'EMPLOYEE_READ',
    'EMPLOYEE_UPDATE',
    'EMPLOYEE_DEACTIVATE',
    'ATTENDANCE_VIEW_ORGANIZATION',
    'LEAVE_APPROVE',
    'EOD_REVIEW',
    'PROJECT_CREATE',
    'PROJECT_READ',
    'PROJECT_UPDATE',
    'TASK_CREATE',
    'TASK_READ',
    'TASK_ASSIGN',
    'TASK_UPDATE',
    'REPORT_VIEW'
)
WHERE r.name = 'SUPER_ADMIN'
  AND r.platform_role = TRUE
ON CONFLICT DO NOTHING;
