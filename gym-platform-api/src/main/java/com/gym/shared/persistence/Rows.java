package com.gym.shared.persistence;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Maps JDBC {@link ResultSet} columns to Java types, for use inside a Spring
 * {@code RowMapper}. Native SQL only (NamedParameterJdbcTemplate) — NO JPA
 * (ADR-0004). Type mapping ONLY: the persistence adapter still runs the query
 * and builds the domain/view object itself.
 */
public final class Rows {

    private Rows() {}

    /** Reads a non-null BIGINT (returns 0 when SQL NULL — use {@link #longOrNull} if nullable). */
    public static long longValue(ResultSet rs, String col) throws SQLException {
        return rs.getLong(col);
    }

    /** Reads a nullable BIGINT. */
    public static Long longOrNull(ResultSet rs, String col) throws SQLException {
        long v = rs.getLong(col);
        return rs.wasNull() ? null : v;
    }

    /** Reads an INT (returns 0 when SQL NULL). */
    public static int intValue(ResultSet rs, String col) throws SQLException {
        return rs.getInt(col);
    }

    /** Reads a VARCHAR/TEXT, may be {@code null}. */
    public static String string(ResultSet rs, String col) throws SQLException {
        return rs.getString(col);
    }

    /** Reads a BOOLEAN (returns false when SQL NULL). */
    public static boolean bool(ResultSet rs, String col) throws SQLException {
        return rs.getBoolean(col);
    }

    /** Reads a NUMERIC, may be {@code null}. */
    public static BigDecimal bigDecimal(ResultSet rs, String col) throws SQLException {
        return rs.getBigDecimal(col);
    }

    /** Reads a DATE as {@link LocalDate}, or {@code null}. */
    public static LocalDate localDate(ResultSet rs, String col) throws SQLException {
        java.sql.Date d = rs.getDate(col);
        return d == null ? null : d.toLocalDate();
    }

    /** Reads a TIMESTAMPTZ as {@link OffsetDateTime} (pgjdbc JDBC 4.2), or {@code null}. */
    public static OffsetDateTime dateTime(ResultSet rs, String col) throws SQLException {
        return rs.getObject(col, OffsetDateTime.class);
    }

    /** Reads a UUID column (accepts UUID or String), or {@code null}. */
    public static UUID uuid(ResultSet rs, String col) throws SQLException {
        Object v = rs.getObject(col);
        if (v == null) return null;
        return v instanceof UUID u ? u : UUID.fromString(v.toString());
    }
}
