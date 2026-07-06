package com.gym.membership.domain;

import java.time.OffsetDateTime;
import java.util.Objects;

public final class Membership {
    private final long id;
    private final String code;
    private final long memberId;
    private final long packagePlanId;
    private final Long contractId;
    private final long saleBranchId;
    private final MembershipStatus status;
    private final OffsetDateTime effectiveFrom;
    private final OffsetDateTime effectiveTo;
    private final OffsetDateTime createdAt;
    private final OffsetDateTime updatedAt;

    private Membership(
            long id,
            String code,
            long memberId,
            long packagePlanId,
            Long contractId,
            long saleBranchId,
            MembershipStatus status,
            OffsetDateTime effectiveFrom,
            OffsetDateTime effectiveTo,
            OffsetDateTime createdAt,
            OffsetDateTime updatedAt
    ) {
        this.id = id;
        this.code = Objects.requireNonNull(code, "code");
        this.memberId = memberId;
        this.packagePlanId = packagePlanId;
        this.contractId = contractId;
        this.saleBranchId = saleBranchId;
        this.status = Objects.requireNonNull(status, "status");
        this.effectiveFrom = effectiveFrom;
        this.effectiveTo = effectiveTo;
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt");
        this.updatedAt = Objects.requireNonNull(updatedAt, "updatedAt");
    }

    public static Membership of(
            long id,
            String code,
            long memberId,
            long packagePlanId,
            Long contractId,
            long saleBranchId,
            MembershipStatus status,
            OffsetDateTime effectiveFrom,
            OffsetDateTime effectiveTo,
            OffsetDateTime createdAt,
            OffsetDateTime updatedAt
    ) {
        return new Membership(
                id,
                code,
                memberId,
                packagePlanId,
                contractId,
                saleBranchId,
                status,
                effectiveFrom,
                effectiveTo,
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

    public long memberId() {
        return memberId;
    }

    public long packagePlanId() {
        return packagePlanId;
    }

    public Long contractId() {
        return contractId;
    }

    public long saleBranchId() {
        return saleBranchId;
    }

    public MembershipStatus status() {
        return status;
    }

    public OffsetDateTime effectiveFrom() {
        return effectiveFrom;
    }

    public OffsetDateTime effectiveTo() {
        return effectiveTo;
    }

    public OffsetDateTime createdAt() {
        return createdAt;
    }

    public OffsetDateTime updatedAt() {
        return updatedAt;
    }
}
