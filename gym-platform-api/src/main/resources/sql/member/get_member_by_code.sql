SELECT
    id,
    code,
    user_account_id,
    full_name,
    phone,
    email,
    gender,
    date_of_birth,
    home_branch_id,
    is_student,
    status,
    created_at,
    updated_at
FROM member.member_profile
WHERE code = :code