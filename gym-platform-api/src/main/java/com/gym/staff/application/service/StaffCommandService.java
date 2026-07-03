package com.gym.staff.application.service;

import com.gym.branch.api.BranchDirectory;
import com.gym.branch.api.BranchRef;
import com.gym.identity.api.RoleDirectory;
import com.gym.identity.api.RoleRef;
import com.gym.identity.api.UserAccountProvisioning;
import com.gym.identity.api.UserAccountRef;
import com.gym.shared.error.DomainException;
import com.gym.shared.validation.Validations;
import com.gym.staff.application.command.AssignBranchRoleCommand;
import com.gym.staff.application.command.CreateStaffCommand;
import com.gym.staff.application.command.LinkStaffUserAccountCommand;
import com.gym.staff.application.port.in.AssignBranchRoleUseCase;
import com.gym.staff.application.port.in.CreateStaffUseCase;
import com.gym.staff.application.port.in.LinkStaffUserAccountUseCase;
import com.gym.staff.application.port.out.ReadStaffPort;
import com.gym.staff.application.port.out.WriteStaffPort;
import com.gym.staff.application.view.StaffDetail;
import com.gym.staff.domain.Staff;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
class StaffCommandService implements CreateStaffUseCase, AssignBranchRoleUseCase, LinkStaffUserAccountUseCase {
    private static final String ROLE_SCOPE_GLOBAL = "GLOBAL";
    private static final String ROLE_SCOPE_BRANCH = "BRANCH";

    private final ReadStaffPort readStaffPort;
    private final WriteStaffPort writeStaffPort;
    private final BranchDirectory branchDirectory;
    private final RoleDirectory roleDirectory;
    private final UserAccountProvisioning userAccountProvisioning;
    private final StaffDetailAssembler detailAssembler;

    StaffCommandService(
            ReadStaffPort readStaffPort,
            WriteStaffPort writeStaffPort,
            BranchDirectory branchDirectory,
            RoleDirectory roleDirectory,
            UserAccountProvisioning userAccountProvisioning,
            StaffDetailAssembler detailAssembler
    ) {
        this.readStaffPort = readStaffPort;
        this.writeStaffPort = writeStaffPort;
        this.branchDirectory = branchDirectory;
        this.roleDirectory = roleDirectory;
        this.userAccountProvisioning = userAccountProvisioning;
        this.detailAssembler = detailAssembler;
    }

    @Override
    @Transactional
    public StaffDetail handle(CreateStaffCommand command) {
        writeStaffPort.insertStaff(command);

        Staff created = readStaffPort.getByEmployeeCode(command.employeeCode())
                .orElseThrow(() -> DomainException.notFound(
                        "Created staff could not be reloaded: " + command.employeeCode()
                ));

        return detailAssembler.toDetail(created, readStaffPort.listAssignments(created.id()));
    }

    @Override
    @Transactional
    public StaffDetail handle(String employeeCode, AssignBranchRoleCommand command) {
        String normalizedCode = Validations.requireText(employeeCode, "employeeCode");
        Staff staff = readStaffPort.getByEmployeeCode(normalizedCode)
                .orElseThrow(() -> DomainException.notFound("Staff not found: " + normalizedCode));

        RoleRef role = roleDirectory.findRefByCode(command.roleCode())
                .orElseThrow(() -> DomainException.validation("roleCode not found: " + command.roleCode()));

        Long branchId = resolveBranchId(role, command.branchCode());

        writeStaffPort.insertAssignment(new WriteStaffPort.NewStaffAssignment(
                staff.id(),
                branchId,
                role.id()
        ));

        return detailAssembler.toDetail(staff, readStaffPort.listAssignments(staff.id()));
    }

    @Override
    @Transactional
    public StaffDetail handle(String employeeCode, LinkStaffUserAccountCommand command) {
        String normalizedCode = Validations.requireText(employeeCode, "employeeCode");
        Staff staff = readStaffPort.getByEmployeeCode(normalizedCode)
                .orElseThrow(() -> DomainException.notFound("Staff not found: " + normalizedCode));

        UserAccountRef account = userAccountProvisioning.ensureStaffAccount(
                command.keycloakUserId(),
                command.username(),
                command.email()
        );

        readStaffPort.getByUserAccountId(account.id())
                .filter(existing -> existing.id() != staff.id())
                .ifPresent(existing -> {
                    throw DomainException.conflict(
                            "User account is already linked to staff: " + existing.employeeCode()
                    );
                });

        if (staff.userAccountId() != null && !staff.userAccountId().equals(account.id())) {
            throw DomainException.conflict("Staff is already linked to another user account");
        }

        boolean linked = writeStaffPort.linkUserAccount(staff.id(), account.id());
        if (!linked) {
            throw DomainException.conflict("Staff is already linked to another user account");
        }

        Staff updated = readStaffPort.getByEmployeeCode(normalizedCode)
                .orElseThrow(() -> DomainException.notFound(
                        "Updated staff could not be reloaded: " + normalizedCode
                ));

        return detailAssembler.toDetail(updated, readStaffPort.listAssignments(updated.id()));
    }

    private Long resolveBranchId(RoleRef role, String branchCode) {
        if (ROLE_SCOPE_GLOBAL.equals(role.scope())) {
            if (branchCode != null) {
                throw DomainException.validation("branchCode must be empty for GLOBAL role: " + role.code());
            }
            return null;
        }

        if (ROLE_SCOPE_BRANCH.equals(role.scope())) {
            String requiredBranchCode = Validations.requireText(branchCode, "branchCode");
            BranchRef branch = branchDirectory.findRefByCode(requiredBranchCode)
                    .orElseThrow(() -> DomainException.validation("branchCode not found: " + requiredBranchCode));
            return branch.id();
        }

        throw DomainException.validation("Unsupported role scope: " + role.scope());
    }
}
