-- TODO(branch-search): consider pg_trgm index when admin keyword search grows.
SELECT
    code,
    name,
    city,
    status,
    created_at,
    count(*) OVER() AS total_count
FROM branch.branch_branch
WHERE (:status IS NULL OR status = :status)
  AND (
      :keyword = ''
      OR code ILIKE '%' || :keyword || '%'
      OR name ILIKE '%' || :keyword || '%'
  )
ORDER BY created_at DESC, id DESC
LIMIT :size OFFSET :offset
