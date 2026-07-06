package com.gym.membership.adapter.out.persistence;

import com.gym.membership.application.view.PackagePlanDetail;
import com.gym.membership.application.view.PackagePlanListItem;
import com.gym.membership.domain.PackagePlan;
import com.gym.membership.domain.PackageType;
import com.gym.shared.persistence.Rows;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

final class PackagePlanRowMappers {
    static final RowMapper<PackagePlan> PACKAGE_PLAN = (rs, rowNum) -> mapPackagePlan(rs);

    static final RowMapper<PackagePlanDetail> PACKAGE_PLAN_DETAIL = (rs, rowNum) -> PackagePlanDetail.fromDomain(
            mapPackagePlan(rs)
    );

    static final RowMapper<PackagePlanListItem> PACKAGE_PLAN_LIST_ITEM = (rs, rowNum) -> new PackagePlanListItem(
            Rows.string(rs, "code"),
            Rows.string(rs, "name"),
            PackageType.valueOf(Rows.string(rs, "package_type")),
            intOrNull(rs, "duration_days"),
            Rows.bigDecimal(rs, "price"),
            Rows.string(rs, "currency"),
            Rows.bool(rs, "is_vip"),
            Rows.bool(rs, "is_student_only"),
            Rows.bool(rs, "is_active")
    );

    private static Integer intOrNull(ResultSet rs, String column) throws SQLException {
        int value = rs.getInt(column);
        return rs.wasNull() ? null : value;
    }

    private static PackagePlan mapPackagePlan(ResultSet rs) throws SQLException {
        return PackagePlan.of(
                Rows.longValue(rs, "id"),
                Rows.string(rs, "code"),
                Rows.string(rs, "name"),
                PackageType.valueOf(Rows.string(rs, "package_type")),
                intOrNull(rs, "duration_days"),
                Rows.bigDecimal(rs, "price"),
                Rows.string(rs, "currency"),
                Rows.bool(rs, "is_vip"),
                Rows.bool(rs, "is_student_only"),
                intOrNull(rs, "total_sessions"),
                intOrNull(rs, "daily_checkin_limit"),
                intOrNull(rs, "private_room_minutes_per_month"),
                intOrNull(rs, "massage_free_per_week"),
                Rows.bool(rs, "installment_allowed"),
                Rows.bool(rs, "is_active"),
                Rows.dateTime(rs, "created_at"),
                Rows.dateTime(rs, "updated_at")
        );
    }

    private PackagePlanRowMappers() {
    }
}
