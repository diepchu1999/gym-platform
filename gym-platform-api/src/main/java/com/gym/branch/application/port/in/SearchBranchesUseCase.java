package com.gym.branch.application.port.in;

import com.gym.branch.application.query.SearchBranchesQuery;
import com.gym.branch.application.view.BranchListItem;
import com.gym.shared.api.PageResponse;

@FunctionalInterface
public interface SearchBranchesUseCase {
    PageResponse<BranchListItem> handle(SearchBranchesQuery query);
}
