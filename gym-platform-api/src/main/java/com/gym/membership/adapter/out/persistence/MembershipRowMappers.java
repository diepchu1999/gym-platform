package com.gym.membership.adapter.out.persistence;

import com.gym.membership.api.MembershipRef;
import com.gym.membership.domain.Membership;
import com.gym.membership.domain.MembershipStatus;
import com.gym.shared.persistence.Rows;
import org.springframework.jdbc.core.RowMapper;

final class MembershipRowMappers {
    static final RowMapper<Membership> MEMBERSHIP = (rs, rowNum) -> Membership.of(
            Rows.longValue(rs, "id"),
            Rows.string(rs, "code"),
            Rows.longValue(rs, "member_id"),
            Rows.longValue(rs, "package_plan_id"),
            Rows.longOrNull(rs, "contract_id"),
            Rows.longValue(rs, "sale_branch_id"),
            MembershipStatus.valueOf(Rows.string(rs, "status")),
            Rows.dateTime(rs, "effective_from"),
            Rows.dateTime(rs, "effective_to"),
            Rows.dateTime(rs, "created_at"),
            Rows.dateTime(rs, "updated_at")
    );

    static final RowMapper<MembershipRef> MEMBERSHIP_REF = (rs, rowNum) -> new MembershipRef(
            Rows.longValue(rs, "id"),
            Rows.string(rs, "code"),
            Rows.longValue(rs, "package_plan_id"),
            MembershipStatus.valueOf(Rows.string(rs, "status")),
            Rows.dateTime(rs, "effective_to")
    );

    private MembershipRowMappers() {
    }
}
