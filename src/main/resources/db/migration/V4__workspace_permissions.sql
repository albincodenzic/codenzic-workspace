INSERT INTO permissions (code, description) VALUES
 ('DEPARTMENT_CREATE','Create departments'),('DEPARTMENT_READ','Read departments'),('DEPARTMENT_UPDATE','Update departments'),
 ('TEAM_CREATE','Create teams'),('TEAM_READ','Read teams'),('TEAM_UPDATE','Update teams')
ON CONFLICT (code) DO NOTHING;
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id,p.id FROM roles r CROSS JOIN permissions p
WHERE r.name='SUPER_ADMIN' AND r.platform_role=TRUE
AND p.code IN ('DEPARTMENT_CREATE','DEPARTMENT_READ','DEPARTMENT_UPDATE','TEAM_CREATE','TEAM_READ','TEAM_UPDATE')
ON CONFLICT DO NOTHING;
