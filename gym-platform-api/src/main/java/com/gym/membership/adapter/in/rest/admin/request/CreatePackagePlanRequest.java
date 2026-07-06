package com.gym.membership.adapter.in.rest.admin.request;

import java.math.BigDecimal;

public record CreatePackagePlanRequest(
        String code,
        String name,
        String packageType,
        Integer durationDays,
        BigDecimal price,
        String currency,
        Boolean vip,
        Boolean studentOnly,
        Integer totalSessions,
        Integer dailyCheckinLimit,
        Integer privateRoomMinutesPerMonth,
        Integer massageFreePerWeek,
        Boolean installmentAllowed,
        Boolean active
) {
}
