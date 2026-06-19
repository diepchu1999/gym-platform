package com.gym.branch.application.port.in;

import com.gym.branch.application.view.BranchOption;
import com.gym.shared.api.ListResponse;

@FunctionalInterface
public interface ListBranchesUseCase {
    ListResponse<BranchOption> handle();
}
