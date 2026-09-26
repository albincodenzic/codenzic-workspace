ALTER TABLE conversation_participants
    ADD COLUMN last_read_at TIMESTAMPTZ;

ALTER TABLE announcements
    ALTER COLUMN published_at DROP NOT NULL,
    ADD COLUMN status VARCHAR(20) NOT NULL DEFAULT 'PUBLISHED',
    ADD COLUMN target_team_id UUID REFERENCES teams(id),
    ADD COLUMN target_department_id UUID REFERENCES departments(id),
    ADD CONSTRAINT ck_announcements_status CHECK (status IN ('DRAFT', 'PUBLISHED', 'ARCHIVED'));

CREATE INDEX idx_announcements_org_status ON announcements(organization_id, status, published_at DESC);

INSERT INTO permissions (code, description) VALUES
    ('ANNOUNCEMENT_UPDATE', 'Update organization announcements'),
    ('ANNOUNCEMENT_PUBLISH', 'Publish organization announcements')
ON CONFLICT (code) DO NOTHING;

INSERT INTO role_permissions (role_id, permission_id)
SELECT role.id, permission.id
FROM roles role
JOIN permissions permission ON permission.code IN (
    'CHAT_CREATE', 'CHAT_READ', 'CHAT_SEND', 'ANNOUNCEMENT_CREATE', 'ANNOUNCEMENT_READ',
    'ANNOUNCEMENT_UPDATE', 'ANNOUNCEMENT_PUBLISH', 'NOTIFICATION_READ', 'NOTIFICATION_UPDATE'
)
WHERE role.name = 'ORGANIZATION_ADMIN' AND role.platform_role = FALSE
ON CONFLICT DO NOTHING;

INSERT INTO role_permissions (role_id, permission_id)
SELECT role.id, permission.id
FROM roles role
JOIN permissions permission ON permission.code IN ('ANNOUNCEMENT_UPDATE', 'ANNOUNCEMENT_PUBLISH')
WHERE role.name = 'SUPER_ADMIN' AND role.platform_role = TRUE
ON CONFLICT DO NOTHING;
