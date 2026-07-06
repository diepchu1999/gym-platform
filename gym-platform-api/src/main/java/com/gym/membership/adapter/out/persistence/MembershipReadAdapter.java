package com.gym.membership.adapter.out.persistence;

import com.gym.membership.api.MembershipRef;
import com.gym.membership.application.port.out.ReadMembershipPort;
import com.gym.membership.domain.Membership;
import com.gym.shared.sql.SqlLoader;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
class MembershipReadAdapter implements ReadMembershipPort {
    private final NamedParameterJdbcTemplate jdbc;
    private final SqlLoader sql;

    MembershipReadAdapter(NamedParameterJdbcTemplate jdbc, SqlLoader sql) {
        this.jdbc = jdbc;
        this.sql = sql;
    }

    @Override
    public Optional<Membership> getByCode(String code) {
        try {
            Membership membership = jdbc.queryForObject(
                    sql.load(MembershipSqlPaths.GET_MEMBERSHIP_BY_CODE),
                    new MapSqlParameterSource("code", code),
                    MembershipRowMappers.MEMBERSHIP
            );
            return Optional.ofNullable(membership);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<Membership> listByMemberId(long memberId) {
        return jdbc.query(
                sql.load(MembershipSqlPaths.LIST_MEMBERSHIPS_BY_MEMBER_ID),
                new MapSqlParameterSource("memberId", memberId),
                MembershipRowMappers.MEMBERSHIP
        );
    }

    @Override
    public boolean hasActiveMembership(long memberId) {
        Boolean exists = jdbc.queryForObject(
                sql.load(MembershipSqlPaths.HAS_ACTIVE_MEMBERSHIP),
                new MapSqlParameterSource("memberId", memberId),
                Boolean.class
        );
        return Boolean.TRUE.equals(exists);
    }

    @Override
    public Optional<MembershipRef> findActiveByMember(long memberId) {
        try {
            MembershipRef membership = jdbc.queryForObject(
                    sql.load(MembershipSqlPaths.FIND_ACTIVE_MEMBERSHIP_BY_MEMBER_ID),
                    new MapSqlParameterSource("memberId", memberId),
                    MembershipRowMappers.MEMBERSHIP_REF
            );
            return Optional.ofNullable(membership);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }
}
