package com.gym.membership.application.port.in;

import com.gym.membership.application.view.MembershipDetail;

@FunctionalInterface
public interface GetMembershipUseCase {
    MembershipDetail handle(String code);
}
