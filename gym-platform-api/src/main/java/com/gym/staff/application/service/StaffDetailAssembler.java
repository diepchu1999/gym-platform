package com.gym.staff.application.service;

import com.gym.branch.api.BranchDirectory;
import com.gym.branch.api.BranchRef;
import com.gym.identity.api.RoleDirectory;
import com.gym.identity.api.RoleRef;
import com.gym.shared.error.DomainException;
import com.gym.staff.application.view.AssignmentView;
import com.gym.staff.application.view.StaffDetail;
import com.gym.staff.domain.Staff;
import com.gym.staff.domain.StaffAssignment;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
class StaffDetailAssembler {
    private static final String ALL_BRANCHES_NAME = "Tất cả chi nhánh";

    private final BranchDirectory branchDirectory;
    private final RoleDirectory roleDirectory;

    StaffDetailAssembler(BranchDirectory branchDirectory, RoleDirectory roleDirectory) {
        this.branchDirectory = branchDirectory;
        this.roleDirectory = roleDirectory;
    }

    StaffDetail toDetail(Staff staff, List<StaffAssignment> assignments) {
        return StaffDetail.fromDomain(
                staff,
                assignments.stream()
                        .map(this::toAssignmentView)
                        .toList()
        );
    }

    private AssignmentView toAssignmentView(StaffAssignment assignment) {
        BranchRef branch = null;
        if (assignment.branchId() != null) {
            branch = branchDirectory.findRefById(assignment.branchId())
                    .orElseThrow(() -> DomainException.notFound(
                            "Branch not found for staff assignment: " + assignment.id()
                    ));
        }

        RoleRef role = roleDirectory.findRefById(assignment.roleId())
                .orElseThrow(() -> DomainException.notFound(
                        "Role not found for staff assignment: " + assignment.id()
                ));

        return new AssignmentView(
                assignment.id(),
                assignment.branchId(),
                branch == null ? null : branch.code(),
                branch == null ? ALL_BRANCHES_NAME : branch.name(),
                assignment.roleId(),
                role.code(),
                role.name(),
                role.scope(),
                assignment.active(),
                assignment.assignedAt()
        );
    }
}
