package com.gym.identity.adapter.out.persistence;

import com.gym.identity.api.RoleDirectory;
import com.gym.identity.api.RolePermissionDirectory;
import com.gym.identity.api.RoleRef;
import com.gym.shared.sql.SqlLoader;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
class RoleReadAdapter implements RoleDirectory, RolePermissionDirectory {
    private final NamedParameterJdbcTemplate jdbc;
    private final SqlLoader sql;

    RoleReadAdapter(NamedParameterJdbcTemplate jdbc, SqlLoader sql) {
        this.jdbc = jdbc;
        this.sql = sql;
    }

    @Override
    public Optional<RoleRef> findRefByCode(String code) {
        try {
            RoleRef role = jdbc.queryForObject(
                    sql.load(RoleSqlPaths.GET_ROLE_BY_CODE),
                    new MapSqlParameterSource("code", code),
                    RoleRowMappers.ROLE_REF
            );
            return Optional.ofNullable(role);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<RoleRef> findRefById(long id) {
        try {
            RoleRef role = jdbc.queryForObject(
                    sql.load(RoleSqlPaths.GET_ROLE_BY_ID),
                    new MapSqlParameterSource("id", id),
                    RoleRowMappers.ROLE_REF
            );
            return Optional.ofNullable(role);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public boolean roleHasPermission(long roleId, String permissionCode) {
        Boolean exists = jdbc.queryForObject(
                sql.load(RoleSqlPaths.EXISTS_ROLE_PERMISSION),
                new MapSqlParameterSource()
                        .addValue("roleId", roleId)
                        .addValue("permissionCode", permissionCode),
                Boolean.class
        );
        return Boolean.TRUE.equals(exists);
    }
}
