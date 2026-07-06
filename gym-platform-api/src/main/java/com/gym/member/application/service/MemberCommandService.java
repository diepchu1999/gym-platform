package com.gym.member.application.service;

import com.gym.branch.api.BranchDirectory;
import com.gym.branch.api.BranchRef;
import com.gym.member.application.command.CreateMemberCommand;
import com.gym.member.application.port.in.CreateMemberUseCase;
import com.gym.member.application.port.out.ReadMemberPort;
import com.gym.member.application.port.out.WriteMemberPort;
import com.gym.member.application.view.MemberDetail;
import com.gym.member.domain.Member;
import com.gym.shared.error.DomainException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
class MemberCommandService implements CreateMemberUseCase {
    private final ReadMemberPort readMemberPort;
    private final WriteMemberPort writeMemberPort;
    private final BranchDirectory branchDirectory;

    MemberCommandService(
            ReadMemberPort readMemberPort,
            WriteMemberPort writeMemberPort,
            BranchDirectory branchDirectory
    ) {
        this.readMemberPort = readMemberPort;
        this.writeMemberPort = writeMemberPort;
        this.branchDirectory = branchDirectory;
    }

    @Override
    @Transactional
    public MemberDetail handle(CreateMemberCommand command) {
        BranchRef homeBranch = branchDirectory.findRefByCode(command.homeBranchCode())
                .orElseThrow(() -> DomainException.validation(
                        "homeBranchCode not found: " + command.homeBranchCode()
                ));
        writeMemberPort.insert(command, homeBranch.id());

        Member created = readMemberPort.getByCode(command.code())
                .orElseThrow(() -> DomainException.notFound(
                        "Created member could not be reloaded: " + command.code()
                ));

        return MemberDetail.fromDomain(created, homeBranch.code(), homeBranch.name());
    }
}
