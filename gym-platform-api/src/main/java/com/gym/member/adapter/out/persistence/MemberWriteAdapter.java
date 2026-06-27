package com.gym.member.adapter.out.persistence;

import com.gym.member.application.command.CreateMemberCommand;
import com.gym.member.application.port.out.WriteMemberPort;
import com.gym.shared.sql.SqlLoader;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Types;

@Repository
class MemberWriteAdapter implements WriteMemberPort {
    private final NamedParameterJdbcTemplate jdbc;
    private final SqlLoader sql;

    MemberWriteAdapter(NamedParameterJdbcTemplate jdbc, SqlLoader sql) {
        this.jdbc = jdbc;
        this.sql = sql;
    }

    @Override
    public long insert(CreateMemberCommand command, long homeBranchId) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("code", command.code(), Types.VARCHAR)
                .addValue("fullName", command.fullName(), Types.VARCHAR)
                .addValue("phone", command.phone(), Types.VARCHAR)
                .addValue("email", command.email(), Types.VARCHAR)
                .addValue("gender", command.gender() == null ? null : command.gender().name(), Types.VARCHAR)
                .addValue("dateOfBirth", command.dateOfBirth(), Types.DATE)
                .addValue("homeBranchId", homeBranchId, Types.BIGINT)
                .addValue("student", command.student(), Types.BOOLEAN)
                .addValue("status", command.status().name(), Types.VARCHAR);

        Long id = jdbc.queryForObject(sql.load(MemberSqlPaths.INSERT_MEMBER), params, Long.class);
        if (id == null) {
            throw new IllegalStateException("Member insert did not return id");
        }
        return id;
    }
}
