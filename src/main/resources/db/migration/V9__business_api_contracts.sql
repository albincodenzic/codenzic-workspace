CREATE TABLE team_members (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    organization_id UUID NOT NULL REFERENCES organizations(id) ON DELETE CASCADE,
    team_id UUID NOT NULL REFERENCES teams(id) ON DELETE CASCADE,
    employee_id UUID NOT NULL REFERENCES employees(id) ON DELETE CASCADE,
    added_by UUID NOT NULL REFERENCES users(id),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    UNIQUE (team_id, employee_id)
);
CREATE INDEX idx_team_members_org_team ON team_members(organization_id, team_id);
CREATE INDEX idx_team_members_employee ON team_members(organization_id, employee_id);

CREATE TABLE task_comments (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    organization_id UUID NOT NULL REFERENCES organizations(id) ON DELETE CASCADE,
    task_id UUID NOT NULL REFERENCES tasks(id) ON DELETE CASCADE,
    author_id UUID NOT NULL REFERENCES users(id),
    body VARCHAR(4000) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);
CREATE INDEX idx_task_comments_task ON task_comments(organization_id, task_id, created_at);

CREATE TABLE task_history (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    organization_id UUID NOT NULL REFERENCES organizations(id) ON DELETE CASCADE,
    task_id UUID NOT NULL REFERENCES tasks(id) ON DELETE CASCADE,
    actor_id UUID NOT NULL REFERENCES users(id),
    field_name VARCHAR(80) NOT NULL,
    old_value VARCHAR(2000),
    new_value VARCHAR(2000),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);
CREATE INDEX idx_task_history_task ON task_history(organization_id, task_id, created_at);

CREATE TABLE leave_types (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    organization_id UUID NOT NULL REFERENCES organizations(id) ON DELETE CASCADE,
    code VARCHAR(30) NOT NULL,
    name VARCHAR(100) NOT NULL,
    annual_allowance INTEGER NOT NULL CHECK (annual_allowance >= 0),
    active BOOLEAN NOT NULL DEFAULT TRUE,
    UNIQUE (organization_id, code)
);
CREATE INDEX idx_leave_types_org ON leave_types(organization_id, active);

CREATE TABLE leave_balances (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    organization_id UUID NOT NULL REFERENCES organizations(id) ON DELETE CASCADE,
    employee_id UUID NOT NULL REFERENCES employees(id) ON DELETE CASCADE,
    leave_type_id UUID NOT NULL REFERENCES leave_types(id) ON DELETE CASCADE,
    balance_year INTEGER NOT NULL,
    allowance_days INTEGER NOT NULL CHECK (allowance_days >= 0),
    UNIQUE (employee_id, leave_type_id, balance_year)
);
CREATE INDEX idx_leave_balances_org_employee ON leave_balances(organization_id, employee_id, balance_year);

ALTER TABLE leave_requests ADD COLUMN review_comment VARCHAR(1000);
ALTER TABLE eod_reports ADD COLUMN reviewed_at TIMESTAMPTZ;
ALTER TABLE eod_reports ADD COLUMN review_comment VARCHAR(1000);

INSERT INTO permissions (code, description) VALUES
    ('TEAM_MEMBER_MANAGE', 'Manage team membership'),
    ('TASK_COMMENT', 'Comment on tasks'),
    ('ATTENDANCE_MONITOR', 'Monitor organization attendance'),
    ('LEAVE_TYPE_MANAGE', 'Manage organization leave types'),
    ('LEAVE_BALANCE_MANAGE', 'Manage employee leave balances'),
    ('LEAVE_MONITOR', 'Monitor organization leave requests'),
    ('EOD_MONITOR', 'Monitor organization end-of-day reports')
ON CONFLICT (code) DO NOTHING;

INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r
CROSS JOIN permissions p
WHERE r.name = 'SUPER_ADMIN'
  AND r.platform_role = TRUE
  AND p.code IN (
      'TEAM_MEMBER_MANAGE', 'TASK_COMMENT', 'ATTENDANCE_MONITOR',
      'LEAVE_TYPE_MANAGE', 'LEAVE_BALANCE_MANAGE', 'LEAVE_MONITOR',
      'EOD_MONITOR'
  )
ON CONFLICT DO NOTHING;
