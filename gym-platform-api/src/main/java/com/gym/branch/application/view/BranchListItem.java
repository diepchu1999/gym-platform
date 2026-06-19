package com.gym.branch.application.view;

import com.gym.branch.domain.BranchStatus;

import java.time.OffsetDateTime;

public record BranchListItem(
        String code,
        String name,
        String city,
        BranchStatus status,
        OffsetDateTime createdAt
) {
}
