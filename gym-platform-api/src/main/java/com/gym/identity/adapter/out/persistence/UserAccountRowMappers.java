package com.gym.identity.adapter.out.persistence;

import com.gym.identity.api.UserAccountRef;
import com.gym.shared.persistence.Rows;
import org.springframework.jdbc.core.RowMapper;

import java.util.UUID;

final class UserAccountRowMappers {
    static final RowMapper<UserAccountRef> USER_ACCOUNT_REF = (rs, rowNum) -> new UserAccountRef(
            Rows.longValue(rs, "id"),
            rs.getObject("keycloak_user_id", UUID.class),
            Rows.string(rs, "account_type"),
            Rows.string(rs, "username"),
            Rows.string(rs, "email"),
            Rows.string(rs, "status")
    );

    private UserAccountRowMappers() {
    }
}
