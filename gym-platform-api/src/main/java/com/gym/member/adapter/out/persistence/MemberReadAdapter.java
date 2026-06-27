package com.gym.member.adapter.out.persistence;

import com.gym.member.api.MemberRef;
import com.gym.member.application.port.out.ReadMemberPort;
import com.gym.member.application.query.SearchMembersCriteria;
import com.gym.member.application.view.MemberListItem;
import com.gym.member.domain.Member;
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
class MemberReadAdapter implements ReadMemberPort {
    private final NamedParameterJdbcTemplate jdbc;
    private final SqlLoader sql;

    MemberReadAdapter(NamedParameterJdbcTemplate jdbc, SqlLoader sql) {
        this.jdbc = jdbc;
        this.sql = sql;
    }

    @Override
    public Optional<Member> getByCode(String code) {
        try {
            Member member = jdbc.queryForObject(
                    sql.load(MemberSqlPaths.GET_MEMBER_BY_CODE),
                    new MapSqlParameterSource("code", code),
                    MemberRowMappers.MEMBER
            );
            return Optional.ofNullable(member);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public PageResponse<MemberListItem> search(SearchMembersCriteria criteria) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("status", criteria.status() == null ? null : criteria.status().name(), Types.VARCHAR)
                .addValue("keyword", criteria.keyword(), Types.VARCHAR)
                .addValue("branchId", criteria.branchId(), Types.BIGINT)
                .addValue("size", criteria.size(), Types.INTEGER)
                .addValue("offset", criteria.pageIndex() * criteria.size(), Types.INTEGER);

        return jdbc.query(sql.load(MemberSqlPaths.SEARCH_MEMBERS), params,
                rs -> {
                    List<MemberListItem> items = new ArrayList<>();
                    long total = 0;
                    while (rs.next()) {
                        if (items.isEmpty()) {
                            total = Rows.longValue(rs, "total_count");
                        }
                        items.add(MemberRowMappers.MEMBER_LIST_ITEM.mapRow(rs, items.size()));
                    }
                    return PageResponse.ofPageIndex(items, total, criteria.pageIndex(), criteria.size());
                });
    }

    @Override
    public boolean existsById(long id) {
        Boolean exists = jdbc.queryForObject(sql.load(MemberSqlPaths.EXISTS_MEMBER_BY_ID),
                new MapSqlParameterSource("id", id),
                Boolean.class);

        return Boolean.TRUE.equals(exists);
    }

    @Override
    public Optional<MemberRef> findRefById(long id) {
        try {
            MemberRef memberRef = jdbc.queryForObject(
                    sql.load(MemberSqlPaths.FIND_MEMBER_REF_BY_ID),
                    new MapSqlParameterSource("id", id),
                    MemberRowMappers.MEMBER_REF
            );
            return Optional.ofNullable(memberRef);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }
}
