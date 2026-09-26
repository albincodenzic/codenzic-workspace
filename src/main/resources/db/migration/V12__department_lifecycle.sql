ALTER TABLE departments
    ADD COLUMN status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    ADD CONSTRAINT ck_departments_status CHECK (status IN ('ACTIVE', 'INACTIVE'));

CREATE INDEX idx_departments_org_status ON departments(organization_id, status);
