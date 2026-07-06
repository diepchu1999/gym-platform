INSERT INTO membership.package_plan (
    code,
    name,
    package_type,
    duration_days,
    price,
    currency,
    is_vip,
    is_student_only,
    total_sessions,
    daily_checkin_limit,
    private_room_minutes_per_month,
    massage_free_per_week,
    installment_allowed,
    is_active
) VALUES (
    :code,
    :name,
    :packageType,
    :durationDays,
    :price,
    :currency,
    :vip,
    :studentOnly,
    :totalSessions,
    :dailyCheckinLimit,
    :privateRoomMinutesPerMonth,
    :massageFreePerWeek,
    :installmentAllowed,
    :active
)
RETURNING id
