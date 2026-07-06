SELECT
    id,
    code,
    member_id,
    package_plan_id,
    contract_id,
    sale_branch_id,
    status,
    effective_from,
    effective_to,
    created_at,
    updated_at
FROM membership.membership
WHERE code = :code
