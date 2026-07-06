-- TODO(staff-search): consider pg_trgm index when admin keyword search grows.
SELECT
    employee_code,
    full_name,
    phone,
    email,
    status,
    created_at,
    count(*) OVER() AS total_count
FROM staff.staff_staff
WHERE (:status IS NULL OR status = :status)
  AND (
      :keyword = ''
      OR employee_code ILIKE '%' || :keyword || '%'
      OR full_name ILIKE '%' || :keyword || '%'
      OR email ILIKE '%' || :keyword || '%'
  )
ORDER BY created_at DESC, id DESC
LIMIT :size OFFSET :offset
