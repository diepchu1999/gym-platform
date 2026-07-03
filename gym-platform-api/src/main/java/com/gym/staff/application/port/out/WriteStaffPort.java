package com.gym.staff.application.port.out;

import com.gym.staff.application.command.CreateStaffCommand;

public interface WriteStaffPort {
    long insertStaff(CreateStaffCommand command);

    boolean linkUserAccount(long staffId, long userAccountId);

    long insertAssignment(NewStaffAssignment assignment);

    record NewStaffAssignment(
            long staffId,
            Long branchId,
            long roleId
    ) {
    }
}
