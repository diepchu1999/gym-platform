package com.gym.membership.application.command;

import com.gym.membership.domain.MembershipStatus;
import com.gym.shared.validation.Enums;

public record UpdateMembershipStatusCommand(MembershipStatus status) {
    public static UpdateMembershipStatusCommand from(String status) {
        return new UpdateMembershipStatusCommand(
                Enums.requireStrict(MembershipStatus.class, "status", status)
        );
    }
}
