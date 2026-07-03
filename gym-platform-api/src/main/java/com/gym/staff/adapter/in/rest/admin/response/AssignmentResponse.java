package com.gym.staff.adapter.in.rest.admin.response;

import com.gym.staff.application.view.AssignmentView;

import java.time.OffsetDateTime;

public record AssignmentResponse(
        String branchCode,
        String branchName,
        String roleCode,
        String roleName,
        String roleScope,
        boolean active,
        OffsetDateTime assignedAt
) {
    public static AssignmentResponse fromDomain(AssignmentView assignment) {
        return new AssignmentResponse(
                assignment.branchCode(),
                assignment.branchName(),
                assignment.roleCode(),
                assignment.roleName(),
                assignment.roleScope(),
                assignment.active(),
                assignment.assignedAt()
        );
    }
}
