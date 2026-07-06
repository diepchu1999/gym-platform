package com.gym.membership.application.port.in;

import com.gym.membership.application.query.ListMemberMembershipsQuery;
import com.gym.membership.application.view.MembershipListItem;
import com.gym.shared.api.ListResponse;

@FunctionalInterface
public interface ListMemberMembershipsUseCase {
    ListResponse<MembershipListItem> handle(ListMemberMembershipsQuery query);
}
