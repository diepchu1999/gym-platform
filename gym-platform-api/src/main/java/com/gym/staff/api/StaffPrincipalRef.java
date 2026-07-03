package com.gym.staff.api;

public record StaffPrincipalRef(
        long id,
        long userAccountId,
        String employeeCode,
        String fullName
) {
}
