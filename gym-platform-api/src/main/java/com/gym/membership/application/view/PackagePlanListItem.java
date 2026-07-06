package com.gym.membership.application.view;

import com.gym.membership.domain.PackageType;

import java.math.BigDecimal;

public record PackagePlanListItem(
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
}
