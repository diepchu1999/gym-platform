package com.gym.staff.application.view;

import java.time.OffsetDateTime;

public record AssignmentView(
        long id,
        Long branchId,
        String branchCode,
        String branchName,
        long roleId,
        String roleCode,
        String roleName,
        String roleScope,
        boolean active,
        OffsetDateTime assignedAt
) {
}
