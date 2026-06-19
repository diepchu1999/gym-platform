package com.gym.branch.application.view;

import com.gym.branch.domain.Branch;
import com.gym.branch.domain.BranchStatus;

import java.time.OffsetDateTime;

public record BranchDetail(
        long id,
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
    public static BranchDetail fromDomain(Branch branch) {
        return new BranchDetail(
                branch.id(),
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
