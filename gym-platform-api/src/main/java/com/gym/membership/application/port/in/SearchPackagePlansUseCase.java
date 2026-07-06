package com.gym.membership.application.port.in;

import com.gym.membership.application.query.SearchPackagePlansQuery;
import com.gym.membership.application.view.PackagePlanListItem;
import com.gym.shared.api.PageResponse;

@FunctionalInterface
public interface SearchPackagePlansUseCase {
    PageResponse<PackagePlanListItem> handle(SearchPackagePlansQuery query);
}
