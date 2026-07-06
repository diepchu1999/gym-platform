package com.gym.membership.api;

import java.util.Optional;

public interface MembershipDirectory {
    boolean hasActiveMembership(long memberId);

    Optional<MembershipRef> findActiveByMember(long memberId);
}
