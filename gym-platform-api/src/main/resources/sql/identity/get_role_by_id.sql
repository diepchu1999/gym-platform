SELECT
    id,
    code,
    name,
    scope
FROM identity.rbac_role
WHERE id = :id