package com.gym.staff.domain;

import java.time.OffsetDateTime;
import java.util.Objects;

public final class StaffAssignment {
    private final long id;
    private final long staffId;
    private final Long branchId;
    private final long roleId;
    private final boolean active;
    private final OffsetDateTime assignedAt;

    private StaffAssignment(
            long id,
            long staffId,
            Long branchId,
            long roleId,
            boolean active,
            OffsetDateTime assignedAt
    ) {
        this.id = id;
        this.staffId = staffId;
        this.branchId = branchId;
        this.roleId = roleId;
        this.active = active;
        this.assignedAt = Objects.requireNonNull(assignedAt, "assignedAt");
    }

    public static StaffAssignment of(
            long id,
            long staffId,
            Long branchId,
            long roleId,
            boolean active,
            OffsetDateTime assignedAt
    ) {
        return new StaffAssignment(
                id,
                staffId,
                branchId,
                roleId,
                active,
                assignedAt
        );
    }

    public long id() {
        return id;
    }

    public long staffId() {
        return staffId;
    }

    public Long branchId() {
        return branchId;
    }

    public long roleId() {
        return roleId;
    }

    public boolean active() {
        return active;
    }

    public OffsetDateTime assignedAt() {
        return assignedAt;
    }
}
