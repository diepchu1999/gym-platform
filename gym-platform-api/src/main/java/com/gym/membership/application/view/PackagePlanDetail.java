package com.gym.membership.application.view;

import com.gym.membership.domain.PackagePlan;
import com.gym.membership.domain.PackageType;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record PackagePlanDetail(
        String code,
        String name,
        PackageType packageType,
        Integer durationDays,
        BigDecimal price,
        String currency,
        boolean vip,
        boolean studentOnly,
        Integer totalSessions,
        Integer dailyCheckinLimit,
        Integer privateRoomMinutesPerMonth,
        Integer massageFreePerWeek,
        boolean installmentAllowed,
        boolean active,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
    public static PackagePlanDetail fromDomain(PackagePlan packagePlan) {
        return new PackagePlanDetail(
                packagePlan.code(),
                packagePlan.name(),
                packagePlan.packageType(),
                packagePlan.durationDays(),
                packagePlan.price(),
                packagePlan.currency(),
                packagePlan.vip(),
                packagePlan.studentOnly(),
                packagePlan.totalSessions(),
                packagePlan.dailyCheckinLimit(),
                packagePlan.privateRoomMinutesPerMonth(),
                packagePlan.massageFreePerWeek(),
                packagePlan.installmentAllowed(),
                packagePlan.active(),
                packagePlan.createdAt(),
                packagePlan.updatedAt()
        );
    }
}
