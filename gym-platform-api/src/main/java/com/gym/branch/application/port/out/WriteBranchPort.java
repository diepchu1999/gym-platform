package com.gym.branch.application.port.out;

import com.gym.branch.application.command.CreateBranchCommand;

public interface WriteBranchPort {
    long insert(CreateBranchCommand command);
}
