package com.gym.membership.adapter.out.persistence;

final class MembershipSqlPaths {
    static final String FIND_ACTIVE_MEMBERSHIP_BY_MEMBER_ID = "sql/membership/find_active_membership_by_member_id.sql";
    static final String GET_MEMBERSHIP_BY_CODE = "sql/membership/get_membership_by_code.sql";
    static final String HAS_ACTIVE_MEMBERSHIP = "sql/membership/has_active_membership.sql";
    static final String INSERT_MEMBERSHIP = "sql/membership/insert_membership.sql";
    static final String LIST_MEMBERSHIPS_BY_MEMBER_ID = "sql/membership/list_memberships_by_member_id.sql";
    static final String UPDATE_MEMBERSHIP_STATUS = "sql/membership/update_membership_status.sql";

    private MembershipSqlPaths() {
    }
}
