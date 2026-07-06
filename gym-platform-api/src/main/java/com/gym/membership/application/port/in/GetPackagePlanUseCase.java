package com.gym.membership.application.port.in;

import com.gym.membership.application.view.PackagePlanDetail;

@FunctionalInterface
public interface GetPackagePlanUseCase {
    PackagePlanDetail handle(String code);
}
