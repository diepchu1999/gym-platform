SELECT
    id,
    code,
    full_name,
    phone,
    status
FROM member.member_profile
WHERE code = :code
