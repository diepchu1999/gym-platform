package com.gym.membership.application.service;

import com.gym.membership.application.port.in.GetPackagePlanUseCase;
import com.gym.membership.application.port.in.ListActivePackagePlansUseCase;
import com.gym.membership.application.port.in.SearchPackagePlansUseCase;
import com.gym.membership.application.port.out.ReadPackagePlanPort;
import com.gym.membership.application.query.SearchPackagePlansQuery;
import com.gym.membership.application.view.PackagePlanDetail;
import com.gym.membership.application.view.PackagePlanListItem;
import com.gym.shared.api.ListResponse;
import com.gym.shared.api.PageResponse;
import com.gym.shared.error.DomainException;
import com.gym.shared.validation.Validations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
class PackagePlanQueryService implements SearchPackagePlansUseCase, GetPackagePlanUseCase, ListActivePackagePlansUseCase {
    private final ReadPackagePlanPort readPackagePlanPort;

    PackagePlanQueryService(ReadPackagePlanPort readPackagePlanPort) {
        this.readPackagePlanPort = readPackagePlanPort;
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<PackagePlanListItem> handle(SearchPackagePlansQuery query) {
        return readPackagePlanPort.search(query);
    }

    @Override
    @Transactional(readOnly = true)
    public PackagePlanDetail handle(String code) {
        String normalizedCode = Validations.requireText(code, "code");
        return readPackagePlanPort.getByCode(normalizedCode)
                .orElseThrow(() -> DomainException.notFound("Package plan not found: " + normalizedCode));
    }

    @Override
    @Transactional(readOnly = true)
    public ListResponse<PackagePlanListItem> handle() {
        return ListResponse.of(readPackagePlanPort.listActive());
    }
}
