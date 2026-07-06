package com.gym.membership.adapter.out.persistence;

import com.gym.membership.application.port.out.ReadPackagePlanPort;
import com.gym.membership.application.query.SearchPackagePlansQuery;
import com.gym.membership.application.view.PackagePlanDetail;
import com.gym.membership.application.view.PackagePlanListItem;
import com.gym.membership.domain.PackagePlan;
import com.gym.shared.api.PageResponse;
import com.gym.shared.persistence.Rows;
import com.gym.shared.sql.SqlLoader;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
class PackagePlanReadAdapter implements ReadPackagePlanPort {
    private final NamedParameterJdbcTemplate jdbc;
    private final SqlLoader sql;

    PackagePlanReadAdapter(NamedParameterJdbcTemplate jdbc, SqlLoader sql) {
        this.jdbc = jdbc;
        this.sql = sql;
    }

    @Override
    public Optional<PackagePlan> findById(long id) {
        try {
            PackagePlan packagePlan = jdbc.queryForObject(
                    sql.load(PackagePlanSqlPaths.GET_PACKAGE_PLAN_BY_ID),
                    new MapSqlParameterSource("id", id),
                    PackagePlanRowMappers.PACKAGE_PLAN
            );
            return Optional.ofNullable(packagePlan);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<PackagePlan> findByCode(String code) {
        try {
            PackagePlan packagePlan = jdbc.queryForObject(
                    sql.load(PackagePlanSqlPaths.GET_PACKAGE_PLAN_BY_CODE),
                    new MapSqlParameterSource("code", code),
                    PackagePlanRowMappers.PACKAGE_PLAN
            );
            return Optional.ofNullable(packagePlan);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<PackagePlanDetail> getByCode(String code) {
        try {
            PackagePlanDetail packagePlan = jdbc.queryForObject(
                    sql.load(PackagePlanSqlPaths.GET_PACKAGE_PLAN_BY_CODE),
                    new MapSqlParameterSource("code", code),
                    PackagePlanRowMappers.PACKAGE_PLAN_DETAIL
            );
            return Optional.ofNullable(packagePlan);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public PageResponse<PackagePlanListItem> search(SearchPackagePlansQuery query) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("packageType", query.packageType() == null ? null : query.packageType().name(), Types.VARCHAR)
                .addValue("active", query.active(), Types.BOOLEAN)
                .addValue("keyword", query.keyword(), Types.VARCHAR)
                .addValue("size", query.size(), Types.INTEGER)
                .addValue("offset", query.pageIndex() * query.size(), Types.INTEGER);

        return jdbc.query(sql.load(PackagePlanSqlPaths.SEARCH_PACKAGE_PLANS), params, rs -> {
            List<PackagePlanListItem> items = new ArrayList<>();
            long total = 0;
            while (rs.next()) {
                if (items.isEmpty()) {
                    total = Rows.longValue(rs, "total_count");
                }
                items.add(PackagePlanRowMappers.PACKAGE_PLAN_LIST_ITEM.mapRow(rs, items.size()));
            }
            return PageResponse.ofPageIndex(items, total, query.pageIndex(), query.size());
        });
    }

    @Override
    public List<PackagePlanListItem> listActive() {
        return jdbc.query(
                sql.load(PackagePlanSqlPaths.LIST_ACTIVE_PACKAGE_PLANS),
                new MapSqlParameterSource(),
                PackagePlanRowMappers.PACKAGE_PLAN_LIST_ITEM
        );
    }
}
