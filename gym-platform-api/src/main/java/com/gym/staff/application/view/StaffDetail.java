package com.gym.staff.application.view;

import com.gym.staff.domain.Staff;
import com.gym.staff.domain.StaffStatus;

import java.time.OffsetDateTime;
import java.util.List;

public record StaffDetail(
        String employeeCode,
        Long userAccountId,
        String fullName,
        String phone,
        String email,
        StaffStatus status,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt,
        List<AssignmentView> assignments
) {
    public static StaffDetail fromDomain(Staff staff, List<AssignmentView> assignments) {
        return new StaffDetail(
                staff.employeeCode(),
                staff.userAccountId(),
                staff.fullName(),
                staff.phone(),
                staff.email(),
                staff.status(),
                staff.createdAt(),
                staff.updatedAt(),
                assignments
        );
    }
}
