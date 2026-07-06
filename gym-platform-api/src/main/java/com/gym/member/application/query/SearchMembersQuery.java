package com.gym.member.application.query;

import com.gym.member.domain.MemberStatus;
import com.gym.shared.api.PageParams;
import com.gym.shared.api.Paged;
import com.gym.shared.api.QueryParams;
import com.gym.shared.validation.Enums;

public record SearchMembersQuery(
        MemberStatus status,
        String keyword,
        String branchCode,
        int page,
        int size
) implements Paged {
    private static final int DEFAULT_SIZE = 20;
    private static final int MAX_SIZE = 100;

    public static SearchMembersQuery from(String status, String keyword, String branchCode, Integer page, Integer size) {
        PageParams pageParams = PageParams.normalize(page, size, DEFAULT_SIZE, MAX_SIZE);
        return new SearchMembersQuery(
                Enums.parseStrict(MemberStatus.class, "status", QueryParams.filterOrNull(status)),
                QueryParams.searchOrEmpty(keyword),
                QueryParams.filterOrNull(branchCode),
                pageParams.page(),
                pageParams.size()
        );
    }
}
