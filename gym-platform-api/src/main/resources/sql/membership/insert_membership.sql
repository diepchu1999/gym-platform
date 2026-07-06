INSERT INTO membership.membership (
    code,
    member_id,
    package_plan_id,
    contract_id,
    sale_branch_id,
    status,
    effective_from,
    effective_to
) VALUES (
    :code,
    :memberId,
    :packagePlanId,
    :contractId,
    :saleBranchId,
    :status,
    :effectiveFrom,
    :effectiveTo
)
RETURNING id
