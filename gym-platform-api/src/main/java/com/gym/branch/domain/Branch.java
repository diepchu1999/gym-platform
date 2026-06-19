package com.gym.branch.domain;

import java.time.OffsetDateTime;
import java.util.Objects;

public final class Branch {
    private final long id;
    private final String code;
    private final String name;
    private final String address;
    private final String district;
    private final String city;
    private final String phone;
    private final boolean open24h;
    private final BranchStatus status;
    private final OffsetDateTime createdAt;
    private final OffsetDateTime updatedAt;

    private Branch(
            long id,
            String code,
            String name,
            String address,
            String district,
            String city,
            String phone,
            boolean open24h,
            BranchStatus status,
            OffsetDateTime createdAt,
            OffsetDateTime updatedAt
    ) {
        this.id = id;
        this.code = Objects.requireNonNull(code, "code");
        this.name = Objects.requireNonNull(name, "name");
        this.address = address;
        this.district = district;
        this.city = Objects.requireNonNull(city, "city");
        this.phone = phone;
        this.open24h = open24h;
        this.status = Objects.requireNonNull(status, "status");
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt");
        this.updatedAt = Objects.requireNonNull(updatedAt, "updatedAt");
    }

    public static Branch of(
            long id,
            String code,
            String name,
            String address,
            String district,
            String city,
            String phone,
            boolean open24h,
            BranchStatus status,
            OffsetDateTime createdAt,
            OffsetDateTime updatedAt
    ) {
        return new Branch(id, code, name, address, district, city, phone, open24h, status, createdAt, updatedAt);
    }

    public long id() {
        return id;
    }

    public String code() {
        return code;
    }

    public String name() {
        return name;
    }

    public String address() {
        return address;
    }

    public String district() {
        return district;
    }

    public String city() {
        return city;
    }

    public String phone() {
        return phone;
    }

    public boolean open24h() {
        return open24h;
    }

    public BranchStatus status() {
        return status;
    }

    public OffsetDateTime createdAt() {
        return createdAt;
    }

    public OffsetDateTime updatedAt() {
        return updatedAt;
    }
}
