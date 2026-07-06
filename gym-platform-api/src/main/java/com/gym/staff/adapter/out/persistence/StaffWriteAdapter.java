package com.gym.staff.adapter.out.persistence;

import com.gym.shared.sql.SqlLoader;
import com.gym.staff.application.command.CreateStaffCommand;
import com.gym.staff.application.port.out.WriteStaffPort;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Types;

@Repository
class StaffWriteAdapter implements WriteStaffPort {
    private final NamedParameterJdbcTemplate jdbc;
    private final SqlLoader sql;

    StaffWriteAdapter(NamedParameterJdbcTemplate jdbc, SqlLoader sql) {
        this.jdbc = jdbc;
        this.sql = sql;
    }

    @Override
    public long insertStaff(CreateStaffCommand command) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("employeeCode", command.employeeCode(), Types.VARCHAR)
                .addValue("fullName", command.fullName(), Types.VARCHAR)
                .addValue("phone", command.phone(), Types.VARCHAR)
                .addValue("email", command.email(), Types.VARCHAR)
                .addValue("status", command.status().name(), Types.VARCHAR);

        Long id = jdbc.queryForObject(sql.load(StaffSqlPaths.INSERT_STAFF), params, Long.class);
        if (id == null) {
            throw new IllegalStateException("Staff insert did not return id");
        }
        return id;
    }

    @Override
    public boolean linkUserAccount(long staffId, long userAccountId) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("staffId", staffId, Types.BIGINT)
                .addValue("userAccountId", userAccountId, Types.BIGINT);

        int updated = jdbc.update(sql.load(StaffSqlPaths.LINK_USER_ACCOUNT), params);
        return updated > 0;
    }

    @Override
    public long insertAssignment(NewStaffAssignment assignment) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("staffId", assignment.staffId(), Types.BIGINT)
                .addValue("branchId", assignment.branchId(), Types.BIGINT)
                .addValue("roleId", assignment.roleId(), Types.BIGINT);

        Long id = jdbc.queryForObject(sql.load(StaffSqlPaths.INSERT_ASSIGNMENT), params, Long.class);
        if (id == null) {
            throw new IllegalStateException("Staff assignment insert did not return id");
        }
        return id;
    }
}
