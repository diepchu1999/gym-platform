package com.gym.branch.adapter.out.persistence;

final class BranchSqlPaths {
    static final String SEARCH_BRANCHES = "sql/branch/search_branches.sql";
    static final String GET_BRANCH_BY_CODE = "sql/branch/get_branch_by_code.sql";
    static final String LIST_ACTIVE_BRANCHES = "sql/branch/list_active_branches.sql";
    static final String INSERT_BRANCH = "sql/branch/insert_branch.sql";
    static final String EXISTS_BRANCH_BY_ID = "sql/branch/exists_branch_by_id.sql";
    static final String FIND_BRANCH_REF_BY_ID = "sql/branch/find_branch_ref_by_id.sql";

    private BranchSqlPaths() {
    }
}
