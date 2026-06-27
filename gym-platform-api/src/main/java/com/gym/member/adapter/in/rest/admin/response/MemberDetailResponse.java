package com.gym.member.adapter.in.rest.admin.response;

import com.gym.member.application.view.MemberDetail;
import com.gym.member.domain.Gender;
import com.gym.member.domain.MemberStatus;

import java.time.LocalDate;
import java.time.OffsetDateTime;

public record MemberDetailResponse(
        String code,
        Long userAccountId,
        String fullName,
        String phone,
        String email,
        Gender gender,
        LocalDate dateOfBirth,
        String homeBranchCode,
        String homeBranchName,
        boolean student,
        MemberStatus status,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
    public static MemberDetailResponse fromDomain(MemberDetail member) {
        return new MemberDetailResponse(
                member.code(),
                member.userAccountId(),
                member.fullName(),
                member.phone(),
                member.email(),
                member.gender(),
                member.dateOfBirth(),
                member.homeBranchCode(),
                member.homeBranchName(),
                member.student(),
                member.status(),
                member.createdAt(),
                member.updatedAt()
        );
    }
}
