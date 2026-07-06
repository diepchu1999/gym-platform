package com.gym.membership.domain;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Objects;

public final class PackagePlan {
    private final long id;
    private final String code;
    private final String name;
    private final PackageType packageType;
    private final Integer durationDays;
    private final BigDecimal price;
    private final String currency;
    private final boolean vip;
    private final boolean studentOnly;
    private final Integer totalSessions;
    private final Integer dailyCheckinLimit;
    private final Integer privateRoomMinutesPerMonth;
    private final Integer massageFreePerWeek;
    private final boolean installmentAllowed;
    private final boolean active;
    private final OffsetDateTime createdAt;
    private final OffsetDateTime updatedAt;

    private PackagePlan(
            long id,
            String code,
            String name,
            PackageType packageType,
            Integer durationDays,
            BigDecimal price,
            String currency,
            boolean vip,
            boolean studentOnly,
            Integer totalSessions,
            Integer dailyCheckinLimit,
            Integer privateRoomMinutesPerMonth,
            Integer massageFreePerWeek,
            boolean installmentAllowed,
            boolean active,
            OffsetDateTime createdAt,
            OffsetDateTime updatedAt
    ) {
        this.id = id;
        this.code = Objects.requireNonNull(code, "code");
        this.name = Objects.requireNonNull(name, "name");
        this.packageType = Objects.requireNonNull(packageType, "packageType");
        this.durationDays = durationDays;
        this.price = Objects.requireNonNull(price, "price");
        this.currency = Objects.requireNonNull(currency, "currency");
        this.vip = vip;
        this.studentOnly = studentOnly;
        this.totalSessions = totalSessions;
        this.dailyCheckinLimit = dailyCheckinLimit;
        this.privateRoomMinutesPerMonth = privateRoomMinutesPerMonth;
        this.massageFreePerWeek = massageFreePerWeek;
        this.installmentAllowed = installmentAllowed;
        this.active = active;
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt");
        this.updatedAt = Objects.requireNonNull(updatedAt, "updatedAt");
    }

    public static PackagePlan of(
            long id,
            String code,
            String name,
            PackageType packageType,
            Integer durationDays,
            BigDecimal price,
            String currency,
            boolean vip,
            boolean studentOnly,
            Integer totalSessions,
            Integer dailyCheckinLimit,
            Integer privateRoomMinutesPerMonth,
            Integer massageFreePerWeek,
            boolean installmentAllowed,
            boolean active,
            OffsetDateTime createdAt,
            OffsetDateTime updatedAt
    ) {
        return new PackagePlan(
                id,
                code,
                name,
                packageType,
                durationDays,
                price,
                currency,
                vip,
                studentOnly,
                totalSessions,
                dailyCheckinLimit,
                privateRoomMinutesPerMonth,
                massageFreePerWeek,
                installmentAllowed,
                active,
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

    public String name() {
        return name;
    }

    public PackageType packageType() {
        return packageType;
    }

    public Integer durationDays() {
        return durationDays;
    }

    public BigDecimal price() {
        return price;
    }

    public String currency() {
        return currency;
    }

    public boolean vip() {
        return vip;
    }

    public boolean studentOnly() {
        return studentOnly;
    }

    public Integer totalSessions() {
        return totalSessions;
    }

    public Integer dailyCheckinLimit() {
        return dailyCheckinLimit;
    }

    public Integer privateRoomMinutesPerMonth() {
        return privateRoomMinutesPerMonth;
    }

    public Integer massageFreePerWeek() {
        return massageFreePerWeek;
    }

    public boolean installmentAllowed() {
        return installmentAllowed;
    }

    public boolean active() {
        return active;
    }

    public OffsetDateTime createdAt() {
        return createdAt;
    }

    public OffsetDateTime updatedAt() {
        return updatedAt;
    }
}
