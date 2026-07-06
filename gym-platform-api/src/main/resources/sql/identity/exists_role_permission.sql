SELECT EXISTS (
    SELECT 1
    FROM identity.rbac_role_permission rp
    JOIN identity.rbac_permission p ON p.id = rp.permission_id
    WHERE rp.role_id = :roleId
      AND p.code = :permissionCode
)
