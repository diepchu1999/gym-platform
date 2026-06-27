SELECT EXISTS (
    SELECT 1
    FROM member.member_profile
    WHERE id = :id
) AS exists