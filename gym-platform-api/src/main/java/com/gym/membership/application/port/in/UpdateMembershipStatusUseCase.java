package com.gym.membership.application.port.in;

import com.gym.membership.application.command.UpdateMembershipStatusCommand;
import com.gym.membership.application.view.MembershipDetail;

@FunctionalInterface
public interface UpdateMembershipStatusUseCase {
    MembershipDetail handle(String code, UpdateMembershipStatusCommand command);
}
