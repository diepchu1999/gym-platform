package com.gym.branch.adapter.in.rest.admin.response;

import com.gym.branch.application.view.BranchListItem;
import com.gym.branch.domain.BranchStatus;

import java.time.OffsetDateTime;

public record BranchListItemResponse(
        String code,
        String name,
        String city,
        BranchStatus status,
        OffsetDateTime createdAt
) {
    public static BranchListItemResponse fromDomain(BranchListItem branch) {
        return new BranchListItemResponse(
                branch.code(),
                branch.name(),
                branch.city(),
                branch.status(),
                branch.createdAt()
        );
    }
}
