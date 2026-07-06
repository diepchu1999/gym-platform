-- DEV bootstrap Super Admin.
-- Same UUID must exist as Keycloak user id in infra/docker/keycloak/import/gym-platform-realm.json.
-- Production should not rely on hardcoded bootstrap data; use an env-driven provisioning runner instead.

INSERT INTO identity.identity_user_account (keycloak_user_id, account_type, username, email)
VALUES (
    '00000000-0000-0000-0000-000000000001',
    'STAFF',
    'superadmin',
    'superadmin@gym-platform.com'
)
ON CONFLICT (keycloak_user_id) DO NOTHING;

INSERT INTO staff.staff_staff (employee_code, full_name, status, user_account_id)
SELECT 'STF-ADMIN', 'System Administrator', 'ACTIVE', a.id
FROM identity.identity_user_account a
WHERE a.keycloak_user_id = '00000000-0000-0000-0000-000000000001'
ON CONFLICT (employee_code) DO NOTHING;

INSERT INTO staff.staff_branch_assignment (staff_id, branch_id, role_id, active)
SELECT s.id, NULL, r.id, true
FROM staff.staff_staff s
CROSS JOIN identity.rbac_role r
WHERE s.employee_code = 'STF-ADMIN'
  AND r.code = 'SUPER_ADMIN'
ON CONFLICT (staff_id, COALESCE(branch_id, 0), role_id) DO NOTHING;
