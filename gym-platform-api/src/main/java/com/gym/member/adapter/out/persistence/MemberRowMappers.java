package com.gym.member.adapter.out.persistence;

import com.gym.member.api.MemberRef;
import com.gym.member.application.view.MemberListItem;
import com.gym.member.domain.Gender;
import com.gym.member.domain.Member;
import com.gym.member.domain.MemberStatus;
import com.gym.shared.persistence.Rows;
import org.springframework.jdbc.core.RowMapper;

final class MemberRowMappers {
    static final RowMapper<Member> MEMBER = (rs, rowNum) -> Member.of(
            Rows.longValue(rs, "id"),
            Rows.string(rs, "code"),
            Rows.longOrNull(rs, "user_account_id"),
            Rows.string(rs, "full_name"),
            Rows.string(rs, "phone"),
            Rows.string(rs, "email"),
            gender(Rows.string(rs, "gender")),
            Rows.localDate(rs, "date_of_birth"),
            Rows.longValue(rs, "home_branch_id"),
            Rows.bool(rs, "is_student"),
            MemberStatus.valueOf(Rows.string(rs, "status")),
            Rows.dateTime(rs, "created_at"),
            Rows.dateTime(rs, "updated_at")
    );

    static final RowMapper<MemberListItem> MEMBER_LIST_ITEM = (rs, rowNum) -> new MemberListItem(
            Rows.string(rs, "code"),
            Rows.string(rs, "full_name"),
            Rows.string(rs, "phone"),
            Rows.string(rs, "email"),
            gender(Rows.string(rs, "gender")),
            MemberStatus.valueOf(Rows.string(rs, "status")),
            Rows.dateTime(rs, "created_at")
    );

    static final RowMapper<MemberRef> MEMBER_REF = (rs, rowNum) -> new MemberRef(
            Rows.longValue(rs, "id"),
            Rows.string(rs, "code"),
            Rows.string(rs, "full_name"),
            Rows.string(rs, "phone"),
            MemberStatus.valueOf(Rows.string(rs, "status"))
    );

    private MemberRowMappers() {
    }

    private static Gender gender(String value) {
        return value == null ? null : Gender.valueOf(value);
    }
}
