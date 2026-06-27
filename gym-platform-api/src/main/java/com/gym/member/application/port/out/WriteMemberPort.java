package com.gym.member.application.port.out;

import com.gym.member.application.command.CreateMemberCommand;

public interface WriteMemberPort {
    long insert(CreateMemberCommand command, long homeBranchId);
}
