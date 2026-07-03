SELECT
    id,
    user_account_id,
    employee_code,
    full_name,
    phone,
    email,
    status,
    created_at,
    updated_at
FROM staff.staff_staff
WHERE user_account_id = :userAccountId
