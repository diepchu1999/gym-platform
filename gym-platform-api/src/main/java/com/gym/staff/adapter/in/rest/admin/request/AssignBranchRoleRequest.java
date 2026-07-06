package com.gym.staff.adapter.in.rest.admin.request;

public record AssignBranchRoleRequest(
        String branchCode,
        String roleCode
) {
}
