SELECT
    code,
    name,
    package_type,
    duration_days,
    price,
    currency,
    is_vip,
    is_student_only,
    is_active,
    count(*) OVER() AS total_count
FROM membership.package_plan
WHERE (:packageType IS NULL OR package_type = :packageType)
  AND (:active IS NULL OR is_active = :active)
  AND (
      :keyword = ''
      OR code ILIKE '%' || :keyword || '%'
      OR name ILIKE '%' || :keyword || '%'
  )
ORDER BY created_at DESC, code ASC
LIMIT :size OFFSET :offset
