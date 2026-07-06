package com.gym.member.api;

import com.gym.member.domain.MemberStatus;

public record MemberRef(
        long id,
        String code,
        String fullName,
        String phone,
        MemberStatus status
) {
}
