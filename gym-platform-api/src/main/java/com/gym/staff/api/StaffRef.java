package com.gym.staff.api;

public record StaffRef(
        long id,
        String employeeCode,
        String fullName
) {
}
