package com.gym.membership.adapter.in.rest.admin.response;

import com.gym.membership.application.view.PackagePlanDetail;
import com.gym.membership.domain.PackageType;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record PackagePlanDetailResponse(
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
    public static PackagePlanDetailResponse fromDomain(PackagePlanDetail packagePlan) {
        return new PackagePlanDetailResponse(
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
