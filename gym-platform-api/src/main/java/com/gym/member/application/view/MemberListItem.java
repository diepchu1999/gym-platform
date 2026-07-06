package com.gym.member.application.view;

import com.gym.member.domain.Gender;
import com.gym.member.domain.MemberStatus;

import java.time.OffsetDateTime;

public record MemberListItem(
        String code,
        String fullName,
        String phone,
        String email,
        Gender gender,
        MemberStatus status,
        OffsetDateTime createdAt
) {
}
