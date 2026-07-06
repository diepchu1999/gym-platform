package com.gym.membership.application.port.in;

import com.gym.membership.application.command.SetPackagePlanActiveCommand;
import com.gym.membership.application.view.PackagePlanDetail;

@FunctionalInterface
public interface SetPackagePlanActiveUseCase {
    PackagePlanDetail handle(String code, SetPackagePlanActiveCommand command);
}
