package com.gym.member.application.port.in;

import com.gym.member.application.command.CreateMemberCommand;
import com.gym.member.application.view.MemberDetail;

@FunctionalInterface
public interface CreateMemberUseCase {
    MemberDetail handle(CreateMemberCommand command);
}
