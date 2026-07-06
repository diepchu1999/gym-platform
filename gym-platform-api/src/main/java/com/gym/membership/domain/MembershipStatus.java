package com.gym.membership.domain;

public enum MembershipStatus {
    PENDING_PAYMENT,
    ACTIVE,
    EXPIRED,
    SUSPENDED,
    CANCELLED;

    public boolean canTransitionTo(MembershipStatus target) {
        return switch (this) {
            case PENDING_PAYMENT -> target == ACTIVE;
            case ACTIVE -> target == SUSPENDED || target == CANCELLED;
            case EXPIRED, SUSPENDED, CANCELLED -> false;
        };
    }
}
