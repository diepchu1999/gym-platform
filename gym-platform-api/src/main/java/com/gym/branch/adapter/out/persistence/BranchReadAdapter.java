package com.gym.branch.adapter.out.persistence;

import com.gym.branch.api.BranchRef;
import com.gym.branch.application.port.out.ReadBranchPort;
import com.gym.branch.application.query.SearchBranchesQuery;
import com.gym.branch.application.view.BranchDetail;
import com.gym.branch.application.view.BranchListItem;
import com.gym.branch.application.view.BranchOption;
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
class BranchReadAdapter implements ReadBranchPort {
    private final NamedParameterJdbcTemplate jdbc;
    private final SqlLoader sql;

    BranchReadAdapter(NamedParameterJdbcTemplate jdbc, SqlLoader sql) {
        this.jdbc = jdbc;
        this.sql = sql;
    }

    @Override
    public Optional<BranchDetail> getByCode(String code) {
        try {
            BranchDetail branch = jdbc.queryForObject(
                    sql.load(BranchSqlPaths.GET_BRANCH_BY_CODE),
                    new MapSqlParameterSource("code", code),
                    BranchRowMappers.BRANCH_DETAIL
            );
            return Optional.ofNullable(branch);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public PageResponse<BranchListItem> search(SearchBranchesQuery query) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("status", query.status() == null ? null : query.status().name(), Types.VARCHAR)
                .addValue("keyword", query.keyword(), Types.VARCHAR)
                .addValue("size", query.size(), Types.INTEGER)
                .addValue("offset", query.pageIndex() * query.size(), Types.INTEGER);

        return jdbc.query(sql.load(BranchSqlPaths.SEARCH_BRANCHES), params, rs -> {
            List<BranchListItem> items = new ArrayList<>();
            long total = 0;
            while (rs.next()) {
                if (items.isEmpty()) {
                    total = Rows.longValue(rs, "total_count");
                }
                items.add(BranchRowMappers.BRANCH_LIST_ITEM.mapRow(rs, items.size()));
            }
            return PageResponse.ofPageIndex(items, total, query.pageIndex(), query.size());
        });
    }

    @Override
    public List<BranchOption> listActive() {
        return jdbc.query(
                sql.load(BranchSqlPaths.LIST_ACTIVE_BRANCHES),
                new MapSqlParameterSource(),
                BranchRowMappers.BRANCH_OPTION
        );
    }

    @Override
    public boolean existsById(long id) {
        Boolean exists = jdbc.queryForObject(
                sql.load(BranchSqlPaths.EXISTS_BRANCH_BY_ID),
                new MapSqlParameterSource("id", id),
                Boolean.class
        );
        return Boolean.TRUE.equals(exists);
    }

    @Override
    public Optional<BranchRef> findRefById(long id) {
        try {
            BranchRef ref = jdbc.queryForObject(
                    sql.load(BranchSqlPaths.FIND_BRANCH_REF_BY_ID),
                    new MapSqlParameterSource("id", id),
                    BranchRowMappers.BRANCH_REF
            );
            return Optional.ofNullable(ref);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }
}
