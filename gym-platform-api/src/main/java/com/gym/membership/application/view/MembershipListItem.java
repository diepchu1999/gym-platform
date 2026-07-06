package com.gym.membership.application.view;

import com.gym.membership.domain.MembershipStatus;
import com.gym.membership.domain.PackageType;

import java.time.OffsetDateTime;

public record MembershipListItem(
        String code,
        String packagePlanCode,
        String packagePlanName,
        PackageType packageType,
        String saleBranchCode,
        String saleBranchName,
        MembershipStatus status,
        OffsetDateTime effectiveFrom,
        OffsetDateTime effectiveTo
) {
}
