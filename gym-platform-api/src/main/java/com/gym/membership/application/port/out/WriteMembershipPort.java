package com.gym.membership.application.port.out;

import com.gym.membership.domain.MembershipStatus;

import java.time.OffsetDateTime;

public interface WriteMembershipPort {
    long insert(NewMembership membership);

    /**
     * Atomically transitions status only when the row still holds {@code expectedStatus}.
     * Returns the number of rows updated (0 = a concurrent transition already moved the row).
     */
    int updateStatus(String code, MembershipStatus expectedStatus, MembershipStatus targetStatus);

    record NewMembership(
            String code,
            long memberId,
            long packagePlanId,
            Long contractId,
            long saleBranchId,
            MembershipStatus status,
            OffsetDateTime effectiveFrom,
            OffsetDateTime effectiveTo
    ) {
    }
}
