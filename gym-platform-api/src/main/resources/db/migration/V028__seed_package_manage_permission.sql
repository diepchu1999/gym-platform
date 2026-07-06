-- Seed package catalog administration permission.
-- SUPER_ADMIN got full permissions in V005, but PACKAGE_MANAGE is introduced later,
-- so it must be granted explicitly here.

INSERT INTO identity.rbac_permission (code, module, description)
VALUES ('PACKAGE_MANAGE', 'membership', 'Manage package plan catalog')
ON CONFLICT (code) DO NOTHING;

INSERT INTO identity.rbac_role_permission (role_id, permission_id)
SELECT r.id, p.id
FROM (VALUES
    ('SUPER_ADMIN', 'PACKAGE_MANAGE'),
    ('OPERATION_MANAGER', 'PACKAGE_MANAGE')
) AS grant_matrix(role_code, permission_code)
JOIN identity.rbac_role r ON r.code = grant_matrix.role_code
JOIN identity.rbac_permission p ON p.code = grant_matrix.permission_code
ON CONFLICT (role_id, permission_id) DO NOTHING;
