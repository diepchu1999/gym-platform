package com.gym.membership.adapter.out.persistence;

import com.gym.membership.application.port.out.WriteMembershipPort;
import com.gym.membership.domain.MembershipStatus;
import com.gym.shared.sql.SqlLoader;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Types;

@Repository
class MembershipWriteAdapter implements WriteMembershipPort {
    private final NamedParameterJdbcTemplate jdbc;
    private final SqlLoader sql;

    MembershipWriteAdapter(NamedParameterJdbcTemplate jdbc, SqlLoader sql) {
        this.jdbc = jdbc;
        this.sql = sql;
    }

    @Override
    public long insert(NewMembership membership) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("code", membership.code(), Types.VARCHAR)
                .addValue("memberId", membership.memberId(), Types.BIGINT)
                .addValue("packagePlanId", membership.packagePlanId(), Types.BIGINT)
                .addValue("contractId", membership.contractId(), Types.BIGINT)
                .addValue("saleBranchId", membership.saleBranchId(), Types.BIGINT)
                .addValue("status", membership.status().name(), Types.VARCHAR)
                .addValue("effectiveFrom", membership.effectiveFrom(), Types.TIMESTAMP_WITH_TIMEZONE)
                .addValue("effectiveTo", membership.effectiveTo(), Types.TIMESTAMP_WITH_TIMEZONE);

        Long id = jdbc.queryForObject(sql.load(MembershipSqlPaths.INSERT_MEMBERSHIP), params, Long.class);
        if (id == null) {
            throw new IllegalStateException("Membership insert did not return id");
        }
        return id;
    }

    @Override
    public int updateStatus(String code, MembershipStatus expectedStatus, MembershipStatus targetStatus) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("code", code, Types.VARCHAR)
                .addValue("expectedStatus", expectedStatus.name(), Types.VARCHAR)
                .addValue("status", targetStatus.name(), Types.VARCHAR);

        return jdbc.update(sql.load(MembershipSqlPaths.UPDATE_MEMBERSHIP_STATUS), params);
    }
}
