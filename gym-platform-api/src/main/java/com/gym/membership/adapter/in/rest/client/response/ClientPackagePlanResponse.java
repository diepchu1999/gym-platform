package com.gym.membership.adapter.in.rest.client.response;

import com.gym.membership.application.view.PackagePlanListItem;
import com.gym.membership.domain.PackageType;

import java.math.BigDecimal;

public record ClientPackagePlanResponse(
        String code,
        String name,
        PackageType packageType,
        Integer durationDays,
        BigDecimal price,
        String currency,
        boolean vip,
        boolean studentOnly
) {
    public static ClientPackagePlanResponse fromDomain(PackagePlanListItem packagePlan) {
        return new ClientPackagePlanResponse(
                packagePlan.code(),
                packagePlan.name(),
                packagePlan.packageType(),
                packagePlan.durationDays(),
                packagePlan.price(),
                packagePlan.currency(),
                packagePlan.vip(),
                packagePlan.studentOnly()
        );
    }
}
