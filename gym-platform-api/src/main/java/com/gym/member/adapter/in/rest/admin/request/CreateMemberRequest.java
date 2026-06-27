package com.gym.member.adapter.in.rest.admin.request;

public record CreateMemberRequest(
        String code,
        String fullName,
        String phone,
        String email,
        String gender,
        String dateOfBirth,
        String homeBranchCode
) {
}
