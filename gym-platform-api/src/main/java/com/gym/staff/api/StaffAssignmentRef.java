package com.gym.staff.api;

public record StaffAssignmentRef(
        long id,
        long staffId,
        Long branchId,
        long roleId
) {
}
