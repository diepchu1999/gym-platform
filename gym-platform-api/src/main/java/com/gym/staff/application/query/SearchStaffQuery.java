package com.gym.staff.application.query;

import com.gym.shared.api.PageParams;
import com.gym.shared.api.Paged;
import com.gym.shared.api.QueryParams;
import com.gym.shared.validation.Enums;
import com.gym.staff.domain.StaffStatus;

public record SearchStaffQuery(
        StaffStatus status,
        String keyword,
        int page,
        int size
) implements Paged {
    private static final int DEFAULT_SIZE = 20;
    private static final int MAX_SIZE = 100;

    public static SearchStaffQuery from(String status, String keyword, Integer page, Integer size) {
        PageParams pageParams = PageParams.normalize(page, size, DEFAULT_SIZE, MAX_SIZE);
        return new SearchStaffQuery(
                Enums.parseStrict(StaffStatus.class, "status", QueryParams.filterOrNull(status)),
                QueryParams.searchOrEmpty(keyword),
                pageParams.page(),
                pageParams.size()
        );
    }
}