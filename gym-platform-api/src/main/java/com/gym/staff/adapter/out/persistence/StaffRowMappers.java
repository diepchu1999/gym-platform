package com.gym.staff.adapter.out.persistence;

import com.gym.shared.persistence.Rows;
import com.gym.staff.api.StaffRef;
import com.gym.staff.application.view.StaffListItem;
import com.gym.staff.domain.Staff;
import com.gym.staff.domain.StaffAssignment;
import com.gym.staff.domain.StaffStatus;
import org.springframework.jdbc.core.RowMapper;

final class StaffRowMappers {
    static final RowMapper<Staff> STAFF = (rs, rowNum) -> Staff.of(
            Rows.longValue(rs, "id"),
            Rows.longOrNull(rs, "user_account_id"),
            Rows.string(rs, "employee_code"),
            Rows.string(rs, "full_name"),
            Rows.string(rs, "phone"),
            Rows.string(rs, "email"),
            StaffStatus.valueOf(Rows.string(rs, "status")),
            Rows.dateTime(rs, "created_at"),
            Rows.dateTime(rs, "updated_at")
    );

    static final RowMapper<StaffListItem> STAFF_LIST_ITEM = (rs, rowNum) -> new StaffListItem(
            Rows.string(rs, "employee_code"),
            Rows.string(rs, "full_name"),
            Rows.string(rs, "phone"),
            Rows.string(rs, "email"),
            StaffStatus.valueOf(Rows.string(rs, "status")),
            Rows.dateTime(rs, "created_at")
    );

    static final RowMapper<StaffAssignment> STAFF_ASSIGNMENT = (rs, rowNum) -> StaffAssignment.of(
            Rows.longValue(rs, "id"),
            Rows.longValue(rs, "staff_id"),
            Rows.longOrNull(rs, "branch_id"),
            Rows.longValue(rs, "role_id"),
            Rows.bool(rs, "active"),
            Rows.dateTime(rs, "assigned_at")
    );

    static final RowMapper<StaffRef> STAFF_REF = (rs, rowNum) -> new StaffRef(
            Rows.longValue(rs, "id"),
            Rows.string(rs, "employee_code"),
            Rows.string(rs, "full_name")
    );

    private StaffRowMappers() {
    }
}
