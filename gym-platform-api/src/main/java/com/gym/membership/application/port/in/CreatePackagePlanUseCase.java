package com.gym.membership.application.port.in;

import com.gym.membership.application.command.CreatePackagePlanCommand;
import com.gym.membership.application.view.PackagePlanDetail;

@FunctionalInterface
public interface CreatePackagePlanUseCase {
    PackagePlanDetail handle(CreatePackagePlanCommand command);
}
