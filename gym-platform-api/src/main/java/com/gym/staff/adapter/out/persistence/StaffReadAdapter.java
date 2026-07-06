package com.gym.staff.adapter.out.persistence;

import com.gym.shared.api.PageResponse;
import com.gym.shared.persistence.Rows;
import com.gym.shared.sql.SqlLoader;
import com.gym.staff.api.StaffRef;
import com.gym.staff.application.port.out.ReadStaffPort;
import com.gym.staff.application.query.SearchStaffQuery;
import com.gym.staff.application.view.StaffListItem;
import com.gym.staff.domain.Staff;
import com.gym.staff.domain.StaffAssignment;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
class StaffReadAdapter implements ReadStaffPort {
    private final NamedParameterJdbcTemplate jdbc;
    private final SqlLoader sql;

    StaffReadAdapter(NamedParameterJdbcTemplate jdbc, SqlLoader sql) {
        this.jdbc = jdbc;
        this.sql = sql;
    }

    @Override
    public Optional<Staff> getByEmployeeCode(String employeeCode) {
        try {
            Staff staff = jdbc.queryForObject(
                    sql.load(StaffSqlPaths.GET_STAFF_BY_EMPLOYEE_CODE),
                    new MapSqlParameterSource("employeeCode", employeeCode),
                    StaffRowMappers.STAFF
            );
            return Optional.ofNullable(staff);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<Staff> getByUserAccountId(long userAccountId) {
        try {
            Staff staff = jdbc.queryForObject(
                    sql.load(StaffSqlPaths.GET_STAFF_BY_USER_ACCOUNT_ID),
                    new MapSqlParameterSource("userAccountId", userAccountId),
                    StaffRowMappers.STAFF
            );
            return Optional.ofNullable(staff);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public PageResponse<StaffListItem> search(SearchStaffQuery query) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("status", query.status() == null ? null : query.status().name(), Types.VARCHAR)
                .addValue("keyword", query.keyword(), Types.VARCHAR)
                .addValue("size", query.size(), Types.INTEGER)
                .addValue("offset", query.pageIndex() * query.size(), Types.INTEGER);

        return jdbc.query(sql.load(StaffSqlPaths.SEARCH_STAFF), params, rs -> {
            List<StaffListItem> items = new ArrayList<>();
            long total = 0;
            while (rs.next()) {
                if (items.isEmpty()) {
                    total = Rows.longValue(rs, "total_count");
                }
                items.add(StaffRowMappers.STAFF_LIST_ITEM.mapRow(rs, items.size()));
            }
            return PageResponse.ofPageIndex(items, total, query.pageIndex(), query.size());
        });
    }

    @Override
    public List<StaffAssignment> listAssignments(long staffId) {
        return jdbc.query(
                sql.load(StaffSqlPaths.LIST_ASSIGNMENTS),
                new MapSqlParameterSource("staffId", staffId),
                StaffRowMappers.STAFF_ASSIGNMENT
        );
    }

    @Override
    public boolean existsById(long id) {
        Boolean exists = jdbc.queryForObject(
                sql.load(StaffSqlPaths.EXISTS_STAFF_BY_ID),
                new MapSqlParameterSource("id", id),
                Boolean.class
        );
        return Boolean.TRUE.equals(exists);
    }

    @Override
    public Optional<StaffRef> findRefById(long id) {
        try {
            StaffRef ref = jdbc.queryForObject(
                    sql.load(StaffSqlPaths.FIND_STAFF_REF_BY_ID),
                    new MapSqlParameterSource("id", id),
                    StaffRowMappers.STAFF_REF
            );
            return Optional.ofNullable(ref);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }
}
