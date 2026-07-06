package com.gym.member.domain;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Objects;

public final class Member {
    private final long id;
    private final String code;
    private final Long userAccountId;
    private final String fullName;
    private final String phone;
    private final String email;
    private final Gender gender;
    private final LocalDate dateOfBirth;
    private final long homeBranchId;
    private final boolean student;
    private final MemberStatus status;
    private final OffsetDateTime createdAt;
    private final OffsetDateTime updatedAt;

    private Member(
            long id,
            String code,
            Long userAccountId,
            String fullName,
            String phone,
            String email,
            Gender gender,
            LocalDate dateOfBirth,
            long homeBranchId,
            boolean student,
            MemberStatus status,
            OffsetDateTime createdAt,
            OffsetDateTime updatedAt
    ) {
        this.id = id;
        this.code = Objects.requireNonNull(code, "code");
        this.userAccountId = userAccountId;
        this.fullName = Objects.requireNonNull(fullName, "fullName");
        this.phone = Objects.requireNonNull(phone, "phone");
        this.email = email;
        this.gender = gender;
        this.dateOfBirth = dateOfBirth;
        this.homeBranchId = homeBranchId;
        this.student = student;
        this.status = Objects.requireNonNull(status, "status");
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt");
        this.updatedAt = Objects.requireNonNull(updatedAt, "updatedAt");
    }

    public static Member of(
            long id,
            String code,
            Long userAccountId,
            String fullName,
            String phone,
            String email,
            Gender gender,
            LocalDate dateOfBirth,
            long homeBranchId,
            boolean student,
            MemberStatus status,
            OffsetDateTime createdAt,
            OffsetDateTime updatedAt
    ) {
        return new Member(
                id,
                code,
                userAccountId,
                fullName,
                phone,
                email,
                gender,
                dateOfBirth,
                homeBranchId,
                student,
                status,
                createdAt,
                updatedAt
        );
    }

    public long id() {
        return id;
    }

    public String code() {
        return code;
    }

    public Long userAccountId() {
        return userAccountId;
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

    public Gender gender() {
        return gender;
    }

    public LocalDate dateOfBirth() {
        return dateOfBirth;
    }

    public long homeBranchId() {
        return homeBranchId;
    }

    public boolean student() {
        return student;
    }

    public MemberStatus status() {
        return status;
    }

    public OffsetDateTime createdAt() {
        return createdAt;
    }

    public OffsetDateTime updatedAt() {
        return updatedAt;
    }

}
