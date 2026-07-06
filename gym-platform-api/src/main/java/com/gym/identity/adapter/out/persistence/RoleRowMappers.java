package com.gym.identity.adapter.out.persistence;

import com.gym.identity.api.RoleRef;
import com.gym.shared.persistence.Rows;
import org.springframework.jdbc.core.RowMapper;

final class RoleRowMappers {
    static final RowMapper<RoleRef> ROLE_REF = (rs, rowNum) -> new RoleRef(
            Rows.longValue(rs, "id"),
            Rows.string(rs, "code"),
            Rows.string(rs, "name"),
            Rows.string(rs, "scope")
    );

    private RoleRowMappers() {
    }
}
