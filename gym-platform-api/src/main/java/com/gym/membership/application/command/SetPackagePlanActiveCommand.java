package com.gym.membership.application.command;

import com.gym.shared.validation.Validations;

public record SetPackagePlanActiveCommand(boolean active) {
    public static SetPackagePlanActiveCommand from(Boolean active) {
        return new SetPackagePlanActiveCommand(Validations.requireNonNull(active, "active"));
    }
}
