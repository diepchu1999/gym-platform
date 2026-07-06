package com.gym.branch.application.service;

import com.gym.branch.application.command.CreateBranchCommand;
import com.gym.branch.application.port.in.CreateBranchUseCase;
import com.gym.branch.application.port.out.ReadBranchPort;
import com.gym.branch.application.port.out.WriteBranchPort;
import com.gym.branch.application.view.BranchDetail;
import com.gym.shared.error.DomainException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
class BranchCommandService implements CreateBranchUseCase {
    private final ReadBranchPort readBranchPort;
    private final WriteBranchPort writeBranchPort;

    BranchCommandService(ReadBranchPort readBranchPort, WriteBranchPort writeBranchPort) {
        this.readBranchPort = readBranchPort;
        this.writeBranchPort = writeBranchPort;
    }

    @Override
    @Transactional
    public BranchDetail handle(CreateBranchCommand command) {
        writeBranchPort.insert(command);
        return readBranchPort.getByCode(command.code())
                .orElseThrow(() -> DomainException.notFound("Created branch could not be reloaded: " + command.code()));
    }
}
