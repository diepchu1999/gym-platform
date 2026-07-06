SELECT
    id,
    code,
    package_plan_id,
    status,
    effective_to
FROM membership.membership
WHERE member_id = :memberId
  AND status = 'ACTIVE'
  AND (effective_from IS NULL OR effective_from <= now())
  AND (effective_to IS NULL OR effective_to >= now())
ORDER BY effective_to DESC NULLS FIRST, id DESC
LIMIT 1
