SELECT
    code,
    name
FROM branch.branch_branch
WHERE status = 'ACTIVE'
ORDER BY name ASC, code ASC
