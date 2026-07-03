package com.gym.identity.adapter.out.persistence;

import com.gym.identity.api.UserAccountProvisioning;
import com.gym.identity.api.UserAccountRef;
import com.gym.shared.error.DomainException;
import com.gym.shared.sql.SqlLoader;
import com.gym.shared.validation.Validations;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Types;
import java.util.Optional;
import java.util.UUID;

@Repository
class UserAccountProvisioningAdapter implements UserAccountProvisioning {
    private static final String ACCOUNT_TYPE_STAFF = "STAFF";

    private final NamedParameterJdbcTemplate jdbc;
    private final SqlLoader sql;

    UserAccountProvisioningAdapter(NamedParameterJdbcTemplate jdbc, SqlLoader sql) {
        this.jdbc = jdbc;
        this.sql = sql;
    }

    @Override
    public UserAccountRef ensureStaffAccount(UUID keycloakUserId, String username, String email) {
        UserAccountRef existing = findByKeycloakUserId(keycloakUserId).orElse(null);
        if (existing != null) {
            if (!ACCOUNT_TYPE_STAFF.equals(existing.accountType())) {
                throw DomainException.conflict("Keycloak user is already linked to non-staff account");
            }
            return existing;
        }

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("keycloakUserId", keycloakUserId, Types.OTHER)
                .addValue("accountType", ACCOUNT_TYPE_STAFF, Types.VARCHAR)
                .addValue("username", Validations.trimToNull(username), Types.VARCHAR)
                .addValue("email", Validations.trimToNull(email), Types.VARCHAR);

        return jdbc.queryForObject(
                sql.load(UserAccountSqlPaths.INSERT_USER_ACCOUNT),
                params,
                UserAccountRowMappers.USER_ACCOUNT_REF
        );
    }

    private Optional<UserAccountRef> findByKeycloakUserId(UUID keycloakUserId) {
        try {
            return Optional.ofNullable(jdbc.queryForObject(
                    sql.load(UserAccountSqlPaths.FIND_BY_KEYCLOAK_USER_ID),
                    new MapSqlParameterSource("keycloakUserId", keycloakUserId),
                    UserAccountRowMappers.USER_ACCOUNT_REF
            ));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }
}