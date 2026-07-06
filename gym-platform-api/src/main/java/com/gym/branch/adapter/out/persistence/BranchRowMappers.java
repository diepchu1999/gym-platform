package com.gym.branch.adapter.out.persistence;

import com.gym.branch.api.BranchRef;
import com.gym.branch.application.view.BranchDetail;
import com.gym.branch.application.view.BranchListItem;
import com.gym.branch.application.view.BranchOption;
import com.gym.branch.domain.Branch;
import com.gym.branch.domain.BranchStatus;
import com.gym.shared.persistence.Rows;
import org.springframework.jdbc.core.RowMapper;

final class BranchRowMappers {
    static final RowMapper<BranchDetail> BRANCH_DETAIL = (rs, rowNum) -> BranchDetail.fromDomain(Branch.of(
            Rows.longValue(rs, "id"),
            Rows.string(rs, "code"),
            Rows.string(rs, "name"),
            Rows.string(rs, "address"),
            Rows.string(rs, "district"),
            Rows.string(rs, "city"),
            Rows.string(rs, "phone"),
            Rows.bool(rs, "open_24h"),
            BranchStatus.valueOf(Rows.string(rs, "status")),
            Rows.dateTime(rs, "created_at"),
            Rows.dateTime(rs, "updated_at")
    ));

    static final RowMapper<BranchListItem> BRANCH_LIST_ITEM = (rs, rowNum) -> new BranchListItem(
            Rows.string(rs, "code"),
            Rows.string(rs, "name"),
            Rows.string(rs, "city"),
            BranchStatus.valueOf(Rows.string(rs, "status")),
            Rows.dateTime(rs, "created_at")
    );

    static final RowMapper<BranchOption> BRANCH_OPTION = (rs, rowNum) -> new BranchOption(
            Rows.string(rs, "code"),
            Rows.string(rs, "name")
    );

    static final RowMapper<BranchRef> BRANCH_REF = (rs, rowNum) -> new BranchRef(
            Rows.longValue(rs, "id"),
            Rows.string(rs, "code"),
            Rows.string(rs, "name")
    );

    private BranchRowMappers() {
    }
}
