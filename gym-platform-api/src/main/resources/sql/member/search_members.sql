-- TODO(member-search): consider pg_trgm index when admin keyword search grows.
SELECT
    code,
    full_name,
    phone,
    email,
    gender,
    status,
    created_at,
    count(*) OVER() AS total_count
FROM member.member_profile
WHERE (:status IS NULL OR status = :status)
  AND (:branchId IS NULL OR home_branch_id = :branchId)
  AND (
    :keyword = ''
        OR code ILIKE '%' || :keyword || '%'
        OR full_name ILIKE '%' || :keyword || '%'
        OR phone ILIKE '%' || :keyword || '%'
        OR email ILIKE '%' || :keyword || '%'
    )
ORDER BY created_at DESC, id DESC
LIMIT :size OFFSET :offset