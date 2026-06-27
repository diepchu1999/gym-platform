INSERT INTO member.member_profile (
    code,
    full_name,
    phone,
    email,
    gender,
    date_of_birth,
    home_branch_id,
    is_student,
    status
) VALUES (
    :code,
    :fullName,
    :phone,
    :email,
    :gender,
    :dateOfBirth,
    :homeBranchId,
    :student,
    :status
)
RETURNING id
