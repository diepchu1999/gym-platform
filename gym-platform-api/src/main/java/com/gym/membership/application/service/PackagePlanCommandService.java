package com.gym.membership.application.service;

import com.gym.membership.application.command.CreatePackagePlanCommand;
import com.gym.membership.application.command.SetPackagePlanActiveCommand;
import com.gym.membership.application.command.UpdatePackagePlanCommand;
import com.gym.membership.application.port.in.CreatePackagePlanUseCase;
import com.gym.membership.application.port.in.SetPackagePlanActiveUseCase;
import com.gym.membership.application.port.in.UpdatePackagePlanUseCase;
import com.gym.membership.application.port.out.ReadPackagePlanPort;
import com.gym.membership.application.port.out.WritePackagePlanPort;
import com.gym.membership.application.view.PackagePlanDetail;
import com.gym.shared.error.DomainException;
import com.gym.shared.validation.Validations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
class PackagePlanCommandService implements CreatePackagePlanUseCase, UpdatePackagePlanUseCase, SetPackagePlanActiveUseCase {
    private final ReadPackagePlanPort readPackagePlanPort;
    private final WritePackagePlanPort writePackagePlanPort;

    PackagePlanCommandService(ReadPackagePlanPort readPackagePlanPort, WritePackagePlanPort writePackagePlanPort) {
        this.readPackagePlanPort = readPackagePlanPort;
        this.writePackagePlanPort = writePackagePlanPort;
    }

    @Override
    @Transactional
    public PackagePlanDetail handle(CreatePackagePlanCommand command) {
        writePackagePlanPort.insert(command);
        return readPackagePlanPort.getByCode(command.code())
                .orElseThrow(() -> DomainException.notFound(
                        "Created package plan could not be reloaded: " + command.code()
                ));
    }

    @Override
    @Transactional
    public PackagePlanDetail handle(String code, UpdatePackagePlanCommand command) {
        String normalizedCode = Validations.requireText(code, "code");
        ensureExists(normalizedCode);
        writePackagePlanPort.update(normalizedCode, command);
        return reload(normalizedCode);
    }

    @Override
    @Transactional
    public PackagePlanDetail handle(String code, SetPackagePlanActiveCommand command) {
        String normalizedCode = Validations.requireText(code, "code");
        ensureExists(normalizedCode);
        writePackagePlanPort.setActive(normalizedCode, command.active());
        return reload(normalizedCode);
    }

    private void ensureExists(String code) {
        if (readPackagePlanPort.getByCode(code).isEmpty()) {
            throw DomainException.notFound("Package plan not found: " + code);
        }
    }

    private PackagePlanDetail reload(String code) {
        return readPackagePlanPort.getByCode(code)
                .orElseThrow(() -> DomainException.notFound("Package plan could not be reloaded: " + code));
    }
}
