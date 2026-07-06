package com.gym.member.application.port.in;

import com.gym.member.application.query.SearchMembersQuery;
import com.gym.member.application.view.MemberListItem;
import com.gym.shared.api.PageResponse;

@FunctionalInterface
public interface SearchMembersUseCase {
    PageResponse<MemberListItem> handle(SearchMembersQuery query);
}
