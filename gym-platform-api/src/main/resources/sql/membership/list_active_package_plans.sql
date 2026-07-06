SELECT
    code,
    name,
    package_type,
    duration_days,
    price,
    currency,
    is_vip,
    is_student_only,
    is_active
FROM membership.package_plan
WHERE is_active = true
ORDER BY package_type ASC, price ASC, code ASC
