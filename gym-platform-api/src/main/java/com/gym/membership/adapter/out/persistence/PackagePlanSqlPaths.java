package com.gym.membership.adapter.out.persistence;

final class PackagePlanSqlPaths {
    static final String GET_PACKAGE_PLAN_BY_CODE = "sql/membership/get_package_plan_by_code.sql";
    static final String GET_PACKAGE_PLAN_BY_ID = "sql/membership/get_package_plan_by_id.sql";
    static final String INSERT_PACKAGE_PLAN = "sql/membership/insert_package_plan.sql";
    static final String LIST_ACTIVE_PACKAGE_PLANS = "sql/membership/list_active_package_plans.sql";
    static final String SEARCH_PACKAGE_PLANS = "sql/membership/search_package_plans.sql";
    static final String SET_PACKAGE_PLAN_ACTIVE = "sql/membership/set_package_plan_active.sql";
    static final String UPDATE_PACKAGE_PLAN = "sql/membership/update_package_plan.sql";

    private PackagePlanSqlPaths() {
    }
}
