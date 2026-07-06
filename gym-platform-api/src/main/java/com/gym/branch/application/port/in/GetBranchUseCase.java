package com.gym.branch.application.port.in;

import com.gym.branch.application.view.BranchDetail;

@FunctionalInterface
public interface GetBranchUseCase {
    BranchDetail handle(String code);
}
