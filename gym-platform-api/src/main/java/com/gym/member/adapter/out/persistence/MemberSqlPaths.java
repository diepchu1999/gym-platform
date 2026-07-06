package com.gym.member.adapter.out.persistence;

final class MemberSqlPaths {
    static final String GET_MEMBER_BY_CODE = "sql/member/get_member_by_code.sql";
    static final String SEARCH_MEMBERS = "sql/member/search_members.sql";
    static final String EXISTS_MEMBER_BY_ID = "sql/member/exists_member_by_id.sql";
    static final String FIND_MEMBER_REF_BY_CODE = "sql/member/find_member_ref_by_code.sql";
    static final String FIND_MEMBER_REF_BY_ID = "sql/member/find_member_ref_by_id.sql";
    static final String INSERT_MEMBER = "sql/member/insert_member.sql";

    private MemberSqlPaths() {
    }
}
