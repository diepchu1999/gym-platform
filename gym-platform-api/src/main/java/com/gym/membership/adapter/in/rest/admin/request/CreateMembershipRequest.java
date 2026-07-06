package com.gym.membership.adapter.in.rest.admin.request;

public record CreateMembershipRequest(
        String memberCode,
        String packagePlanCode,
        String saleBranchCode,
        String effectiveFrom
) {
}
