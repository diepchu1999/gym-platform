package com.gym.membership.application.port.out;

import com.gym.membership.application.command.CreatePackagePlanCommand;
import com.gym.membership.application.command.UpdatePackagePlanCommand;

public interface WritePackagePlanPort {
    long insert(CreatePackagePlanCommand command);

    void update(String code, UpdatePackagePlanCommand command);

    void setActive(String code, boolean active);
}
