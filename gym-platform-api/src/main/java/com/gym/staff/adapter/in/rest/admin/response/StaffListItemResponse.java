package com.gym.staff.adapter.in.rest.admin.response;

import com.gym.staff.application.view.StaffListItem;
import com.gym.staff.domain.StaffStatus;

import java.time.OffsetDateTime;

public record StaffListItemResponse(
        String employeeCode,
        String fullName,
        String phone,
        String email,
        StaffStatus status,
        OffsetDateTime createdAt
) {
    public static StaffListItemResponse fromDomain(StaffListItem staff) {
        return new StaffListItemResponse(
                staff.employeeCode(),
                staff.fullName(),
                staff.phone(),
                staff.email(),
                staff.status(),
                staff.createdAt()
        );
    }
}
