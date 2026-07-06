package com.gym.membership.application.port.out;

import com.gym.membership.api.MembershipRef;
import com.gym.membership.domain.Membership;

import java.util.List;
import java.util.Optional;

public interface ReadMembershipPort {
    Optional<Membership> getByCode(String code);

    List<Membership> listByMemberId(long memberId);

    boolean hasActiveMembership(long memberId);

    Optional<MembershipRef> findActiveByMember(long memberId);
}
