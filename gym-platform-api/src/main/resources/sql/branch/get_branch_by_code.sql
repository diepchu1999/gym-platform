SELECT
    id,
    code,
    name,
    address,
    district,
    city,
    phone,
    open_24h,
    status,
    created_at,
    updated_at
FROM branch.branch_branch
WHERE code = :code
