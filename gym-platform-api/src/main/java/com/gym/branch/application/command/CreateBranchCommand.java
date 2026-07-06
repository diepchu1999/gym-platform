package com.gym.branch.application.command;

import com.gym.branch.domain.BranchStatus;
import com.gym.shared.validation.Validations;

public record CreateBranchCommand(
        String code,
        String name,
        String address,
        String district,
        String city,
        String phone,
        boolean open24h,
        BranchStatus status
) {
    private static final String DEFAULT_CITY = "Ho Chi Minh City";

    public static CreateBranchCommand from(
            String code,
            String name,
            String address,
            String district,
            String city,
            String phone,
            Boolean open24h
    ) {
        String normalizedCity = Validations.trimToNull(city);
        return new CreateBranchCommand(
                Validations.requireText(code, "code"),
                Validations.requireText(name, "name"),
                Validations.trimToNull(address),
                Validations.trimToNull(district),
                normalizedCity == null ? DEFAULT_CITY : normalizedCity,
                Validations.optionalPhone(phone),
                open24h == null || open24h,
                BranchStatus.ACTIVE
        );
    }
}
