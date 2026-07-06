package com.gym.staff.application.port.in;

import com.gym.staff.application.command.CreateStaffCommand;
import com.gym.staff.application.view.StaffDetail;

@FunctionalInterface
public interface CreateStaffUseCase {
    StaffDetail handle(CreateStaffCommand command);
}
