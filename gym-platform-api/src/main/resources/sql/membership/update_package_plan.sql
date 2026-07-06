UPDATE membership.package_plan
SET name = :name,
    package_type = :packageType,
    duration_days = :durationDays,
    price = :price,
    currency = :currency,
    is_vip = :vip,
    is_student_only = :studentOnly,
    total_sessions = :totalSessions,
    daily_checkin_limit = :dailyCheckinLimit,
    private_room_minutes_per_month = :privateRoomMinutesPerMonth,
    massage_free_per_week = :massageFreePerWeek,
    installment_allowed = :installmentAllowed
WHERE code = :code
