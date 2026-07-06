INSERT INTO staff.staff_branch_assignment (
    staff_id,
    branch_id,
    role_id
) VALUES (
    :staffId,
    :branchId,
    :roleId
)
RETURNING id
