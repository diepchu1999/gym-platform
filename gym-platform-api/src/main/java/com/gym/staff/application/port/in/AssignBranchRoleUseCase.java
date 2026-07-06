package com.gym.staff.application.port.in;

import com.gym.staff.application.command.AssignBranchRoleCommand;
import com.gym.staff.application.view.StaffDetail;

@FunctionalInterface
public interface AssignBranchRoleUseCase {
    StaffDetail handle(String employeeCode, AssignBranchRoleCommand command);
}
