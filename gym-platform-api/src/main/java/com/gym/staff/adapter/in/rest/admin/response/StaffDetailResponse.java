package com.gym.staff.adapter.in.rest.admin.response;

import com.gym.staff.application.view.StaffDetail;
import com.gym.staff.domain.StaffStatus;

import java.time.OffsetDateTime;
import java.util.List;

public record StaffDetailResponse(
        String employeeCode,
        Long userAccountId,
        String fullName,
        String phone,
        String email,
        StaffStatus status,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt,
        List<AssignmentResponse> assignments
) {
    public static StaffDetailResponse fromDomain(StaffDetail staff) {
        return new StaffDetailResponse(
                staff.employeeCode(),
                staff.userAccountId(),
                staff.fullName(),
                staff.phone(),
                staff.email(),
                staff.status(),
                staff.createdAt(),
                staff.updatedAt(),
                staff.assignments().stream()
                        .map(AssignmentResponse::fromDomain)
                        .toList()
        );
    }
}
