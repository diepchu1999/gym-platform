package com.gym.branch.application.port.in;

import com.gym.branch.application.command.CreateBranchCommand;
import com.gym.branch.application.view.BranchDetail;

@FunctionalInterface
public interface CreateBranchUseCase {
    BranchDetail handle(CreateBranchCommand command);
}
