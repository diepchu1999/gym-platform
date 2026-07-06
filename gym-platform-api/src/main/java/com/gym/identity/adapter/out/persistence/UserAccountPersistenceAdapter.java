package com.gym.identity.adapter.out.persistence;

import com.gym.identity.api.UserAccountRef;
import com.gym.identity.application.port.out.ReadUserAccountPort;
import com.gym.identity.application.port.out.WriteUserAccountPort;
import com.gym.shared.sql.SqlLoader;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Types;
import java.util.Optional;
import java.util.UUID;

@Repository
class UserAccountPersistenceAdapter implements ReadUserAccountPort, WriteUserAccountPort {
    private final NamedParameterJdbcTemplate jdbc;
    private final SqlLoader sql;

    UserAccountPersistenceAdapter(NamedParameterJdbcTemplate jdbc, SqlLoader sql) {
        this.jdbc = jdbc;
        this.sql = sql;
    }

    @Override
    public Optional<UserAccountRef> findByKeycloakUserId(UUID keycloakUserId) {
        try {
            UserAccountRef account = jdbc.queryForObject(
                    sql.load(UserAccountSqlPaths.FIND_BY_KEYCLOAK_USER_ID),
                    new MapSqlParameterSource("keycloakUserId", keycloakUserId),
                    UserAccountRowMappers.USER_ACCOUNT_REF
            );
            return Optional.ofNullable(account);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public UserAccountRef insert(NewUserAccount account) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("keycloakUserId", account.keycloakUserId(), Types.OTHER)
                .addValue("accountType", account.accountType(), Types.VARCHAR)
                .addValue("username", account.username(), Types.VARCHAR)
                .addValue("email", account.email(), Types.VARCHAR);

        UserAccountRef created = jdbc.queryForObject(
                sql.load(UserAccountSqlPaths.INSERT_USER_ACCOUNT),
                params,
                UserAccountRowMappers.USER_ACCOUNT_REF
        );
        if (created == null) {
            throw new IllegalStateException("User account insert did not return account");
        }
        return created;
    }
}
