package com.gym.membership.adapter.in.rest.admin.response;

import com.gym.membership.application.view.PackagePlanListItem;
import com.gym.membership.domain.PackageType;

import java.math.BigDecimal;

public record PackagePlanListItemResponse(
        String code,
        String name,
        PackageType packageType,
        Integer durationDays,
        BigDecimal price,
        String currency,
        boolean vip,
        boolean studentOnly,
        boolean active
) {
    public static PackagePlanListItemResponse fromDomain(PackagePlanListItem packagePlan) {
        return new PackagePlanListItemResponse(
                packagePlan.code(),
                packagePlan.name(),
                packagePlan.packageType(),
                packagePlan.durationDays(),
                packagePlan.price(),
                packagePlan.currency(),
                packagePlan.vip(),
                packagePlan.studentOnly(),
                packagePlan.active()
        );
    }
}
