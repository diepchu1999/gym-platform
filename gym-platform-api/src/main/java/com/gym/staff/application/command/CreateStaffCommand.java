package com.gym.staff.application.command;

import com.gym.shared.validation.Validations;
import com.gym.staff.domain.StaffStatus;

public record CreateStaffCommand(
        String employeeCode,
        String fullName,
        String phone,
        String email,
        StaffStatus status
) {
    public static CreateStaffCommand from(
            String employeeCode,
            String fullName,
            String phone,
            String email
    ) {
        return new CreateStaffCommand(
                Validations.requireText(employeeCode, "employeeCode"),
                Validations.requireText(fullName, "fullName"),
                Validations.optionalPhone(phone),
                Validations.trimToNull(email),
                StaffStatus.ACTIVE
        );
    }
}
