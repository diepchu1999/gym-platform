package com.gym.member.application.view;

import com.gym.member.domain.Gender;
import com.gym.member.domain.Member;
import com.gym.member.domain.MemberStatus;

import java.time.LocalDate;
import java.time.OffsetDateTime;

public record MemberDetail(
        long id,
        String code,
        Long userAccountId,
        String fullName,
        String phone,
        String email,
        Gender gender,
        LocalDate dateOfBirth,
        long homeBranchId,
        String homeBranchCode,
        String homeBranchName,
        boolean student,
        MemberStatus status,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
    public static MemberDetail fromDomain(Member member, String homeBranchCode, String homeBranchName) {
        return new MemberDetail(
                member.id(),
                member.code(),
                member.userAccountId(),
                member.fullName(),
                member.phone(),
                member.email(),
                member.gender(),
                member.dateOfBirth(),
                member.homeBranchId(),
                homeBranchCode,
                homeBranchName,
                member.student(),
                member.status(),
                member.createdAt(),
                member.updatedAt()
        );
    }
}
