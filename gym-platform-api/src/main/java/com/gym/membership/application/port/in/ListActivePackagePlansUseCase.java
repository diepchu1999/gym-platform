package com.gym.membership.application.port.in;

import com.gym.membership.application.view.PackagePlanListItem;
import com.gym.shared.api.ListResponse;

@FunctionalInterface
public interface ListActivePackagePlansUseCase {
    ListResponse<PackagePlanListItem> handle();
}
