package com.gym.member.application.command;

import com.gym.member.domain.Gender;
import com.gym.member.domain.MemberStatus;
import com.gym.shared.validation.Enums;
import com.gym.shared.validation.Validations;

import java.time.LocalDate;

public record CreateMemberCommand(
        String code,
        String fullName,
        String phone,
        String email,
        Gender gender,
        LocalDate dateOfBirth,
        String homeBranchCode,
        boolean student,
        MemberStatus status
) {
    public static CreateMemberCommand from(
            String code,
            String fullName,
            String phone,
            String email,
            String gender,
            String dateOfBirth,
            String homeBranchCode
    ) {
        return new CreateMemberCommand(
                Validations.requireText(code, "code"),
                Validations.requireText(fullName, "fullName"),
                Validations.requirePhone(phone),
                Validations.trimToNull(email),
                Enums.parseStrict(Gender.class, "gender", gender),
                Validations.optionalDate(dateOfBirth, "dateOfBirth"),
                Validations.requireText(homeBranchCode, "homeBranchCode"),
                false,
                MemberStatus.REGISTERED
        );
    }
}
