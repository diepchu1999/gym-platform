package com.gym.staff.adapter.in.rest.admin.request;

public record CreateStaffRequest(
        String employeeCode,
        String fullName,
        String phone,
        String email
) {
}
