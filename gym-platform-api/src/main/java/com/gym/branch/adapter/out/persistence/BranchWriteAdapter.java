package com.gym.branch.adapter.out.persistence;

import com.gym.branch.application.command.CreateBranchCommand;
import com.gym.branch.application.port.out.WriteBranchPort;
import com.gym.shared.sql.SqlLoader;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Types;

@Repository
class BranchWriteAdapter implements WriteBranchPort {
    private final NamedParameterJdbcTemplate jdbc;
    private final SqlLoader sql;

    BranchWriteAdapter(NamedParameterJdbcTemplate jdbc, SqlLoader sql) {
        this.jdbc = jdbc;
        this.sql = sql;
    }

    @Override
    public long insert(CreateBranchCommand command) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("code", command.code(), Types.VARCHAR)
                .addValue("name", command.name(), Types.VARCHAR)
                .addValue("address", command.address(), Types.VARCHAR)
                .addValue("district", command.district(), Types.VARCHAR)
                .addValue("city", command.city(), Types.VARCHAR)
                .addValue("phone", command.phone(), Types.VARCHAR)
                .addValue("open24h", command.open24h(), Types.BOOLEAN)
                .addValue("status", command.status().name(), Types.VARCHAR);

        Long id = jdbc.queryForObject(sql.load(BranchSqlPaths.INSERT_BRANCH), params, Long.class);
        if (id == null) {
            throw new IllegalStateException("Branch insert did not return id");
        }
        return id;
    }
}
