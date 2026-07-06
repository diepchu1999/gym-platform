package com.gym.membership.application.port.in;

import com.gym.membership.application.command.UpdatePackagePlanCommand;
import com.gym.membership.application.view.PackagePlanDetail;

@FunctionalInterface
public interface UpdatePackagePlanUseCase {
    PackagePlanDetail handle(String code, UpdatePackagePlanCommand command);
}
