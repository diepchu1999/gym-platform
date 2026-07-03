SELECT
    id,
    code,
    name,
    scope
FROM identity.rbac_role
WHERE code = :code