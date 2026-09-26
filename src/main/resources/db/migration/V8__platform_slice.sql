CREATE TABLE company_settings (
    organization_id UUID PRIMARY KEY REFERENCES organizations(id) ON DELETE CASCADE,
    timezone VARCHAR(80) NOT NULL DEFAULT 'UTC',
    currency VARCHAR(3) NOT NULL DEFAULT 'USD',
    work_week JSONB NOT NULL DEFAULT '["MONDAY","TUESDAY","WEDNESDAY","THURSDAY","FRIDAY"]'::jsonb,
    logo_url VARCHAR(500),
    updated_by UUID REFERENCES users(id),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);
CREATE TABLE audit_logs (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    organization_id UUID REFERENCES organizations(id),
    actor_id UUID REFERENCES users(id),
    action VARCHAR(80) NOT NULL,
    resource_type VARCHAR(80) NOT NULL,
    resource_id UUID,
    details JSONB NOT NULL DEFAULT '{}'::jsonb,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);
CREATE INDEX idx_audit_logs_scope ON audit_logs(organization_id, created_at DESC);
CREATE INDEX idx_audit_logs_actor ON audit_logs(actor_id, created_at DESC);
INSERT INTO permissions (code, description) VALUES
 ('DASHBOARD_VIEW','View organization dashboard'),('REPORT_VIEW','View organization reports'),
 ('COMPANY_SETTINGS_READ','Read company settings'),('COMPANY_SETTINGS_UPDATE','Update company settings'),
 ('AUDIT_LOG_READ','Read audit logs')
ON CONFLICT (code) DO NOTHING;
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id,p.id FROM roles r CROSS JOIN permissions p
WHERE r.name='SUPER_ADMIN' AND r.platform_role=TRUE
AND p.code IN ('DASHBOARD_VIEW','REPORT_VIEW','COMPANY_SETTINGS_READ','COMPANY_SETTINGS_UPDATE','AUDIT_LOG_READ')
ON CONFLICT DO NOTHING;
