package com.gym.membership.adapter.in.rest.admin.response;

import com.gym.membership.application.view.MembershipDetail;
import com.gym.membership.domain.MembershipStatus;
import com.gym.membership.domain.PackageType;

import java.time.OffsetDateTime;

public record MembershipDetailResponse(
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
    public static MembershipDetailResponse fromDomain(MembershipDetail membership) {
        return new MembershipDetailResponse(
                membership.code(),
                membership.memberCode(),
                membership.memberName(),
                membership.packagePlanCode(),
                membership.packagePlanName(),
                membership.packageType(),
                membership.saleBranchCode(),
                membership.saleBranchName(),
                membership.status(),
                membership.effectiveFrom(),
                membership.effectiveTo(),
                membership.createdAt(),
                membership.updatedAt()
        );
    }
}
