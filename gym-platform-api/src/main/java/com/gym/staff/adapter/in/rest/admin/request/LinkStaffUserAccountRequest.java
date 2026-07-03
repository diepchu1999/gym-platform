package com.gym.staff.adapter.in.rest.admin.request;

public record LinkStaffUserAccountRequest(
        String keycloakUserId,
        String username,
        String email
) {
}
