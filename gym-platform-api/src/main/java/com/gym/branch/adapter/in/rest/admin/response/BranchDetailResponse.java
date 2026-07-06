package com.gym.branch.adapter.in.rest.admin.response;

import com.gym.branch.application.view.BranchDetail;
import com.gym.branch.domain.BranchStatus;

import java.time.OffsetDateTime;

public record BranchDetailResponse(
        String code,
        String name,
        String address,
        String district,
        String city,
        String phone,
        boolean open24h,
        BranchStatus status,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
    public static BranchDetailResponse fromDomain(BranchDetail branch) {
        return new BranchDetailResponse(
                branch.code(),
                branch.name(),
                branch.address(),
                branch.district(),
                branch.city(),
                branch.phone(),
                branch.open24h(),
                branch.status(),
                branch.createdAt(),
                branch.updatedAt()
        );
    }
}
