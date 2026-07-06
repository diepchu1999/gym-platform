package com.gym.staff.application.port.in;

import com.gym.staff.application.view.StaffDetail;

@FunctionalInterface
public interface GetStaffUseCase {
    StaffDetail handle(String employeeCode);
}
