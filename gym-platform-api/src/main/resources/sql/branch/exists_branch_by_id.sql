SELECT EXISTS (
    SELECT 1
    FROM branch.branch_branch
    WHERE id = :id
) AS exists
