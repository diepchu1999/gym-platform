SELECT EXISTS (
    SELECT 1
    FROM staff.staff_staff
    WHERE id = :id
) AS exists
