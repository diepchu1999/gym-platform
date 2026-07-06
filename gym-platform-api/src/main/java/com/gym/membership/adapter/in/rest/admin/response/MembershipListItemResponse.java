package com.gym.membership.adapter.in.rest.admin.response;

import com.gym.membership.application.view.MembershipListItem;
import com.gym.membership.domain.MembershipStatus;
import com.gym.membership.domain.PackageType;

import java.time.OffsetDateTime;

public record MembershipListItemResponse(
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
    public static MembershipListItemResponse fromDomain(MembershipListItem membership) {
        return new MembershipListItemResponse(
                membership.code(),
                membership.packagePlanCode(),
                membership.packagePlanName(),
                membership.packageType(),
                membership.saleBranchCode(),
                membership.saleBranchName(),
                membership.status(),
                membership.effectiveFrom(),
                membership.effectiveTo()
        );
    }
}
