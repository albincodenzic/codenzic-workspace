ALTER TABLE projects
    ADD COLUMN manager_id UUID REFERENCES employees(id),
    ADD COLUMN progress INTEGER NOT NULL DEFAULT 0,
    ADD CONSTRAINT ck_projects_progress CHECK (progress BETWEEN 0 AND 100),
    ADD CONSTRAINT ck_projects_status CHECK (status IN ('PLANNING', 'ACTIVE', 'ON_HOLD', 'COMPLETED', 'CANCELLED'));

CREATE TABLE project_members (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    organization_id UUID NOT NULL REFERENCES organizations(id) ON DELETE CASCADE,
    project_id UUID NOT NULL REFERENCES projects(id) ON DELETE CASCADE,
    employee_id UUID NOT NULL REFERENCES employees(id) ON DELETE CASCADE,
    added_by UUID NOT NULL REFERENCES users(id),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    UNIQUE (project_id, employee_id)
);

CREATE INDEX idx_project_members_org_project ON project_members(organization_id, project_id);
CREATE INDEX idx_project_members_org_employee ON project_members(organization_id, employee_id);

UPDATE tasks SET status = 'COMPLETED' WHERE status = 'DONE';
ALTER TABLE tasks
    ADD CONSTRAINT ck_tasks_status CHECK (status IN ('TODO', 'IN_PROGRESS', 'IN_REVIEW', 'COMPLETED', 'BLOCKED', 'CANCELLED')),
    ADD CONSTRAINT ck_tasks_priority CHECK (priority IN ('LOW', 'MEDIUM', 'HIGH', 'URGENT'));

CREATE INDEX idx_tasks_org_project ON tasks(organization_id, project_id);
CREATE INDEX idx_tasks_org_assignee ON tasks(organization_id, assignee_id);
CREATE INDEX idx_tasks_org_status_priority ON tasks(organization_id, status, priority);
