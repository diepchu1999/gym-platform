package com.gym.membership.application.query;

import com.gym.shared.validation.Validations;

public record ListMemberMembershipsQuery(String memberCode) {
    public static ListMemberMembershipsQuery from(String memberCode) {
        return new ListMemberMembershipsQuery(Validations.requireText(memberCode, "memberCode"));
    }
}
