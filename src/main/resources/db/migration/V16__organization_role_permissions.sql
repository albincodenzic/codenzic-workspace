INSERT INTO permissions (code, description) VALUES
    ('LEAVE_TYPE_MANAGE', 'Manage organization leave types'),
    ('LEAVE_BALANCE_MANAGE', 'Manage employee leave balances'),
    ('EOD_READ_SELF', 'Read EOD reports for the current employee'),
    ('EOD_READ_TEAM', 'Read EOD reports for the user''s teams')
ON CONFLICT (code) DO NOTHING;

INSERT INTO role_permissions (role_id, permission_id)
SELECT role.id, permission.id
FROM roles role
JOIN permissions permission ON permission.code IN (
    'LEAVE_TYPE_MANAGE', 'LEAVE_BALANCE_MANAGE'
)
WHERE role.platform_role = FALSE
  AND role.name IN ('ORGANIZATION_ADMIN', 'HR')
ON CONFLICT DO NOTHING;
