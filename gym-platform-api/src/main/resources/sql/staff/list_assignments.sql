SELECT
    id,
    staff_id,
    branch_id,
    role_id,
    active,
    assigned_at
FROM staff.staff_branch_assignment
WHERE staff_id = :staffId
ORDER BY active DESC, assigned_at DESC, id DESC
