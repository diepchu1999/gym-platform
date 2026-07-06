package com.gym.membership.adapter.out.persistence;

import com.gym.membership.application.command.CreatePackagePlanCommand;
import com.gym.membership.application.command.UpdatePackagePlanCommand;
import com.gym.membership.application.port.out.WritePackagePlanPort;
import com.gym.shared.sql.SqlLoader;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Types;

@Repository
class PackagePlanWriteAdapter implements WritePackagePlanPort {
    private final NamedParameterJdbcTemplate jdbc;
    private final SqlLoader sql;

    PackagePlanWriteAdapter(NamedParameterJdbcTemplate jdbc, SqlLoader sql) {
        this.jdbc = jdbc;
        this.sql = sql;
    }

    @Override
    public long insert(CreatePackagePlanCommand command) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("code", command.code(), Types.VARCHAR)
                .addValue("name", command.name(), Types.VARCHAR)
                .addValue("packageType", command.packageType().name(), Types.VARCHAR)
                .addValue("durationDays", command.durationDays(), Types.INTEGER)
                .addValue("price", command.price(), Types.NUMERIC)
                .addValue("currency", command.currency(), Types.VARCHAR)
                .addValue("vip", command.vip(), Types.BOOLEAN)
                .addValue("studentOnly", command.studentOnly(), Types.BOOLEAN)
                .addValue("totalSessions", command.totalSessions(), Types.INTEGER)
                .addValue("dailyCheckinLimit", command.dailyCheckinLimit(), Types.INTEGER)
                .addValue("privateRoomMinutesPerMonth", command.privateRoomMinutesPerMonth(), Types.INTEGER)
                .addValue("massageFreePerWeek", command.massageFreePerWeek(), Types.INTEGER)
                .addValue("installmentAllowed", command.installmentAllowed(), Types.BOOLEAN)
                .addValue("active", command.active(), Types.BOOLEAN);

        Long id = jdbc.queryForObject(sql.load(PackagePlanSqlPaths.INSERT_PACKAGE_PLAN), params, Long.class);
        if (id == null) {
            throw new IllegalStateException("Package plan insert did not return id");
        }
        return id;
    }

    @Override
    public void update(String code, UpdatePackagePlanCommand command) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("code", code, Types.VARCHAR)
                .addValue("name", command.name(), Types.VARCHAR)
                .addValue("packageType", command.packageType().name(), Types.VARCHAR)
                .addValue("durationDays", command.durationDays(), Types.INTEGER)
                .addValue("price", command.price(), Types.NUMERIC)
                .addValue("currency", command.currency(), Types.VARCHAR)
                .addValue("vip", command.vip(), Types.BOOLEAN)
                .addValue("studentOnly", command.studentOnly(), Types.BOOLEAN)
                .addValue("totalSessions", command.totalSessions(), Types.INTEGER)
                .addValue("dailyCheckinLimit", command.dailyCheckinLimit(), Types.INTEGER)
                .addValue("privateRoomMinutesPerMonth", command.privateRoomMinutesPerMonth(), Types.INTEGER)
                .addValue("massageFreePerWeek", command.massageFreePerWeek(), Types.INTEGER)
                .addValue("installmentAllowed", command.installmentAllowed(), Types.BOOLEAN);

        jdbc.update(sql.load(PackagePlanSqlPaths.UPDATE_PACKAGE_PLAN), params);
    }

    @Override
    public void setActive(String code, boolean active) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("code", code, Types.VARCHAR)
                .addValue("active", active, Types.BOOLEAN);

        jdbc.update(sql.load(PackagePlanSqlPaths.SET_PACKAGE_PLAN_ACTIVE), params);
    }
}
