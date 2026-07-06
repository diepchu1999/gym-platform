package com.gym.member.application.port.in;

import com.gym.member.application.view.MemberDetail;

@FunctionalInterface
public interface GetMemberUseCase {
    MemberDetail handle(String code);
}
