package com.gym.staff.domain;

import java.time.OffsetDateTime;
import java.util.Objects;

public final class Staff {
    private final long id;
    private final Long userAccountId;
    private final String employeeCode;
    private final String fullName;
    private final String phone;
    private final String email;
    private final StaffStatus status;
    private final OffsetDateTime createdAt;
    private final OffsetDateTime updatedAt;

    private Staff(
            long id,
            Long userAccountId,
            String employeeCode,
            String fullName,
            String phone,
            String email,
            StaffStatus status,
            OffsetDateTime createdAt,
            OffsetDateTime updatedAt
    ) {
        this.id = id;
        this.userAccountId = userAccountId;
        this.employeeCode = Objects.requireNonNull(employeeCode, "employeeCode");
        this.fullName = Objects.requireNonNull(fullName, "fullName");
        this.phone = phone;
        this.email = email;
        this.status = Objects.requireNonNull(status, "status");
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt");
        this.updatedAt = Objects.requireNonNull(updatedAt, "updatedAt");
    }

    public static Staff of(
            long id,
            Long userAccountId,
            String employeeCode,
            String fullName,
            String phone,
            String email,
            StaffStatus status,
            OffsetDateTime createdAt,
            OffsetDateTime updatedAt
    ) {
        return new Staff(
                id,
                userAccountId,
                employeeCode,
                fullName,
                phone,
                email,
                status,
                createdAt,
                updatedAt
        );
    }

    public long id() {
        return id;
    }

    public Long userAccountId() {
        return userAccountId;
    }

    public String employeeCode() {
        return employeeCode;
    }

    public String fullName() {
        return fullName;
    }

    public String phone() {
        return phone;
    }

    public String email() {
        return email;
    }

    public StaffStatus status() {
        return status;
    }

    public OffsetDateTime createdAt() {
        return createdAt;
    }

    public OffsetDateTime updatedAt() {
        return updatedAt;
    }
}
