CREATE TABLE platform_bootstrap_state (
                                          id SMALLINT PRIMARY KEY CHECK (id = 1),
                                          super_admin_initialized BOOLEAN NOT NULL
);

INSERT INTO platform_bootstrap_state (id, super_admin_initialized)
SELECT 1,
       EXISTS (
           SELECT 1
           FROM user_roles ur
                    JOIN users u ON u.id = ur.user_id
                    JOIN roles r ON r.id = ur.role_id
           WHERE u.platform_user = TRUE
             AND r.name = 'SUPER_ADMIN'
             AND r.platform_role = TRUE
             AND r.organization_id IS NULL
       );