ALTER TABLE employees
    ADD COLUMN date_of_birth DATE;

CREATE INDEX idx_employees_org_birthday ON employees(organization_id, date_of_birth);
