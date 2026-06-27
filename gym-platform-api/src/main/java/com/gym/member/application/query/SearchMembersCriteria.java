package com.gym.member.application.query;

import com.gym.member.domain.MemberStatus;
import com.gym.shared.api.Paged;

public record SearchMembersCriteria(
        MemberStatus status,
        String keyword,
        Long branchId,
        int page,
        int size
) implements Paged {
    public static SearchMembersCriteria from(SearchMembersQuery query, Long branchId) {
        return new SearchMembersCriteria(
                query.status(),
                query.keyword(),
                branchId,
                query.page(),
                query.size()
        );
    }
}
