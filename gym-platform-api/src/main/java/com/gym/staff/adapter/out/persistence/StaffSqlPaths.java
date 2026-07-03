package com.gym.staff.adapter.out.persistence;

final class StaffSqlPaths {
    static final String GET_STAFF_BY_EMPLOYEE_CODE = "sql/staff/get_staff_by_employee_code.sql";
    static final String SEARCH_STAFF = "sql/staff/search_staff.sql";
    static final String LIST_ASSIGNMENTS = "sql/staff/list_assignments.sql";
    static final String EXISTS_STAFF_BY_ID = "sql/staff/exists_staff_by_id.sql";
    static final String FIND_STAFF_REF_BY_ID = "sql/staff/find_staff_ref_by_id.sql";
    static final String INSERT_STAFF = "sql/staff/insert_staff.sql";
    static final String INSERT_ASSIGNMENT = "sql/staff/insert_assignment.sql";

    private StaffSqlPaths() {
    }
}
