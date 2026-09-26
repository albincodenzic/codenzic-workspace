CREATE TABLE conversations (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    organization_id UUID NOT NULL REFERENCES organizations(id),
    title VARCHAR(200),
    created_by UUID NOT NULL REFERENCES users(id),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);
CREATE TABLE conversation_participants (
    conversation_id UUID NOT NULL REFERENCES conversations(id) ON DELETE CASCADE,
    user_id UUID NOT NULL REFERENCES users(id),
    joined_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    PRIMARY KEY (conversation_id, user_id)
);
CREATE TABLE messages (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    conversation_id UUID NOT NULL REFERENCES conversations(id) ON DELETE CASCADE,
    sender_id UUID NOT NULL REFERENCES users(id),
    body VARCHAR(4000) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);
CREATE INDEX idx_conversations_org ON conversations(organization_id);
CREATE INDEX idx_participants_user ON conversation_participants(user_id);
CREATE INDEX idx_messages_conversation ON messages(conversation_id, created_at);

CREATE TABLE announcements (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    organization_id UUID REFERENCES organizations(id),
    scope VARCHAR(20) NOT NULL,
    title VARCHAR(200) NOT NULL,
    content VARCHAR(10000) NOT NULL,
    created_by UUID NOT NULL REFERENCES users(id),
    published_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT ck_announcements_scope CHECK (scope IN ('PLATFORM','ORGANIZATION')),
    CONSTRAINT ck_announcements_org_scope CHECK ((scope = 'PLATFORM' AND organization_id IS NULL) OR (scope = 'ORGANIZATION' AND organization_id IS NOT NULL))
);
CREATE INDEX idx_announcements_visibility ON announcements(scope, organization_id, published_at);

CREATE TABLE notifications (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    organization_id UUID REFERENCES organizations(id),
    type VARCHAR(80) NOT NULL,
    title VARCHAR(200) NOT NULL,
    body VARCHAR(2000) NOT NULL,
    announcement_id UUID REFERENCES announcements(id) ON DELETE SET NULL,
    read_at TIMESTAMPTZ,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);
CREATE INDEX idx_notifications_user ON notifications(user_id, created_at);

INSERT INTO permissions (code, description) VALUES
 ('CHAT_CREATE','Create conversations'),('CHAT_READ','Read conversations'),
 ('CHAT_SEND','Send messages'),('ANNOUNCEMENT_CREATE','Publish announcements'),
 ('ANNOUNCEMENT_READ','Read announcements'),('NOTIFICATION_READ','Read notifications'),
 ('NOTIFICATION_UPDATE','Mark notifications read')
ON CONFLICT (code) DO NOTHING;
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id,p.id FROM roles r CROSS JOIN permissions p
WHERE r.name='SUPER_ADMIN' AND r.platform_role=TRUE
AND p.code IN ('CHAT_CREATE','CHAT_READ','CHAT_SEND','ANNOUNCEMENT_CREATE','ANNOUNCEMENT_READ','NOTIFICATION_READ','NOTIFICATION_UPDATE')
ON CONFLICT DO NOTHING;
