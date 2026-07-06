package com.gym.staff.application.port.in;

import com.gym.shared.api.PageResponse;
import com.gym.staff.application.query.SearchStaffQuery;
import com.gym.staff.application.view.StaffListItem;

@FunctionalInterface
public interface SearchStaffUseCase {
    PageResponse<StaffListItem> handle(SearchStaffQuery query);
}
