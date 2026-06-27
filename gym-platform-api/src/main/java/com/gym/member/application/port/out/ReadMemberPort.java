package com.gym.member.application.port.out;

import com.gym.member.api.MemberRef;
import com.gym.member.application.query.SearchMembersCriteria;
import com.gym.member.application.view.MemberListItem;
import com.gym.member.domain.Member;
import com.gym.shared.api.PageResponse;

import java.util.Optional;

public interface ReadMemberPort {
    Optional<Member> getByCode(String code);

    PageResponse<MemberListItem> search(SearchMembersCriteria criteria);

    boolean existsById(long id);

    Optional<MemberRef> findRefById(long id);
}
