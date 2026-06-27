package com.gym.member.adapter.in.rest.admin.response;

import com.gym.member.application.view.MemberListItem;
import com.gym.member.domain.Gender;
import com.gym.member.domain.MemberStatus;

import java.time.OffsetDateTime;

public record MemberListItemResponse(
        String code,
        String fullName,
        String phone,
        String email,
        Gender gender,
        MemberStatus status,
        OffsetDateTime createdAt
) {
    public static MemberListItemResponse fromDomain(MemberListItem memberListItem) {
        return new MemberListItemResponse(
                memberListItem.code(),
                memberListItem.fullName(),
                memberListItem.phone(),
                memberListItem.email(),
                memberListItem.gender(),
                memberListItem.status(),
                memberListItem.createdAt()
        );
    }
}