package com.gym.staff.application.view;

import com.gym.staff.domain.StaffStatus;

import java.time.OffsetDateTime;

public record StaffListItem(
        String employeeCode,
        String fullName,
        String phone,
        String email,
        StaffStatus status,
        OffsetDateTime createdAt
) {
}
