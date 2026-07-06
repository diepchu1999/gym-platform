package com.gym.staff.application.port.in;

import com.gym.staff.application.command.LinkStaffUserAccountCommand;
import com.gym.staff.application.view.StaffDetail;

@FunctionalInterface
public interface LinkStaffUserAccountUseCase {
    StaffDetail handle(String employeeCode, LinkStaffUserAccountCommand command);
}
