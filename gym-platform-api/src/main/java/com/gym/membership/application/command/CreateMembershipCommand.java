package com.gym.membership.application.command;

import com.gym.shared.validation.Validations;

import java.time.OffsetDateTime;

public record CreateMembershipCommand(
        String memberCode,
        String packagePlanCode,
        String saleBranchCode,
        OffsetDateTime effectiveFrom
) {
    public static CreateMembershipCommand from(
            String memberCode,
            String packagePlanCode,
            String saleBranchCode,
            String effectiveFrom
    ) {
        return new CreateMembershipCommand(
                Validations.requireText(memberCode, "memberCode"),
                Validations.requireText(packagePlanCode, "packagePlanCode"),
                Validations.requireText(saleBranchCode, "saleBranchCode"),
                Validations.requireDateTime(effectiveFrom, "effectiveFrom")
        );
    }
}
