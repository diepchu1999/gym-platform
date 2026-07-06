package com.gym.membership.application.port.out;

import com.gym.membership.application.query.SearchPackagePlansQuery;
import com.gym.membership.application.view.PackagePlanDetail;
import com.gym.membership.application.view.PackagePlanListItem;
import com.gym.membership.domain.PackagePlan;
import com.gym.shared.api.PageResponse;

import java.util.List;
import java.util.Optional;

public interface ReadPackagePlanPort {
    Optional<PackagePlan> findById(long id);

    Optional<PackagePlan> findByCode(String code);

    Optional<PackagePlanDetail> getByCode(String code);

    PageResponse<PackagePlanListItem> search(SearchPackagePlansQuery query);

    List<PackagePlanListItem> listActive();
}
