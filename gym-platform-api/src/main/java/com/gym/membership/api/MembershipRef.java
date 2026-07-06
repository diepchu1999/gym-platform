package com.gym.membership.api;

import com.gym.membership.domain.MembershipStatus;

import java.time.OffsetDateTime;

public record MembershipRef(
        long id,
        String code,
        long packagePlanId,
        MembershipStatus status,
        OffsetDateTime effectiveTo
) {
}
