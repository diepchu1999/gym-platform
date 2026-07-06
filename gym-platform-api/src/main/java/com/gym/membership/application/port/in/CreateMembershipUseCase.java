package com.gym.membership.application.port.in;

import com.gym.membership.application.command.CreateMembershipCommand;
import com.gym.membership.application.view.MembershipDetail;

@FunctionalInterface
public interface CreateMembershipUseCase {
    MembershipDetail handle(CreateMembershipCommand command);
}
