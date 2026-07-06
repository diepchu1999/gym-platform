package com.gym.membership.application.view;

import com.gym.membership.domain.MembershipStatus;
import com.gym.membership.domain.PackageType;

import java.time.OffsetDateTime;

public record MembershipDetail(
        String code,
        String memberCode,
        String memberName,
        String packagePlanCode,
        String packagePlanName,
        PackageType packageType,
        String saleBranchCode,
        String saleBranchName,
        MembershipStatus status,
        OffsetDateTime effectiveFrom,
        OffsetDateTime effectiveTo,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
}
