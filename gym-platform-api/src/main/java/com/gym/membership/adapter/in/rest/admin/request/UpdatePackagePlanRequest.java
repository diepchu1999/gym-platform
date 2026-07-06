package com.gym.membership.adapter.in.rest.admin.request;

import java.math.BigDecimal;

public record UpdatePackagePlanRequest(
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
        Boolean installmentAllowed
) {
}
