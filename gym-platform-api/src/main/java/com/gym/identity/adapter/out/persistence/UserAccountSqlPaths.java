package com.gym.identity.adapter.out.persistence;

final class UserAccountSqlPaths {
    static final String FIND_BY_KEYCLOAK_USER_ID = "sql/identity/find_user_account_by_keycloak_user_id.sql";
    static final String INSERT_USER_ACCOUNT = "sql/identity/insert_user_account.sql";

    private UserAccountSqlPaths() {
    }
}
