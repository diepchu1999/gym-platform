package com.gym.staff.application.command;

import com.gym.shared.validation.Validations;

public record AssignBranchRoleCommand(
        String branchCode,
        String roleCode
) {
    public static AssignBranchRoleCommand from(String branchCode, String roleCode) {
        return new AssignBranchRoleCommand(
                Validations.trimToNull(branchCode),
                Validations.requireText(roleCode, "roleCode")
        );
    }
}
