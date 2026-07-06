package com.gym.membership.application.service;

import com.gym.branch.api.BranchDirectory;
import com.gym.branch.api.BranchRef;
import com.gym.member.api.MemberDirectory;
import com.gym.member.api.MemberRef;
import com.gym.membership.application.command.CreateMembershipCommand;
import com.gym.membership.application.command.UpdateMembershipStatusCommand;
import com.gym.membership.application.port.in.CreateMembershipUseCase;
import com.gym.membership.application.port.in.UpdateMembershipStatusUseCase;
import com.gym.membership.application.port.out.ReadMembershipPort;
import com.gym.membership.application.port.out.ReadPackagePlanPort;
import com.gym.membership.application.port.out.WriteMembershipPort;
import com.gym.membership.application.view.MembershipDetail;
import com.gym.membership.domain.Membership;
import com.gym.membership.domain.MembershipStatus;
import com.gym.membership.domain.PackagePlan;
import com.gym.shared.error.DomainException;
import com.gym.shared.validation.Validations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;

@Service
class MembershipCommandService implements CreateMembershipUseCase, UpdateMembershipStatusUseCase {
    private final ReadMembershipPort readMembershipPort;
    private final WriteMembershipPort writeMembershipPort;
    private final ReadPackagePlanPort readPackagePlanPort;
    private final MemberDirectory memberDirectory;
    private final BranchDirectory branchDirectory;
    private final MembershipDetailAssembler assembler;

    MembershipCommandService(
            ReadMembershipPort readMembershipPort,
            WriteMembershipPort writeMembershipPort,
            ReadPackagePlanPort readPackagePlanPort,
            MemberDirectory memberDirectory,
            BranchDirectory branchDirectory,
            MembershipDetailAssembler assembler
    ) {
        this.readMembershipPort = readMembershipPort;
        this.writeMembershipPort = writeMembershipPort;
        this.readPackagePlanPort = readPackagePlanPort;
        this.memberDirectory = memberDirectory;
        this.branchDirectory = branchDirectory;
        this.assembler = assembler;
    }

    @Override
    @Transactional
    public MembershipDetail handle(CreateMembershipCommand command) {
        MemberRef member = memberDirectory.findRefByCode(command.memberCode())
                .orElseThrow(() -> DomainException.validation("memberCode not found: " + command.memberCode()));
        BranchRef branch = branchDirectory.findRefByCode(command.saleBranchCode())
                .orElseThrow(() -> DomainException.validation("saleBranchCode not found: " + command.saleBranchCode()));
        PackagePlan packagePlan = readPackagePlanPort.findByCode(command.packagePlanCode())
                .orElseThrow(() -> DomainException.validation(
                        "packagePlanCode not found: " + command.packagePlanCode()
                ));

        if (!packagePlan.active()) {
            throw DomainException.validation("Package plan is inactive: " + packagePlan.code());
        }

        OffsetDateTime effectiveTo = calculateEffectiveTo(command.effectiveFrom(), packagePlan.durationDays());
        String membershipCode = MembershipCodeGenerator.next();

        writeMembershipPort.insert(new WriteMembershipPort.NewMembership(
                membershipCode,
                member.id(),
                packagePlan.id(),
                null,
                branch.id(),
                MembershipStatus.PENDING_PAYMENT,
                command.effectiveFrom(),
                effectiveTo
        ));

        return assembler.toDetail(reload(membershipCode));
    }

    @Override
    @Transactional
    public MembershipDetail handle(String code, UpdateMembershipStatusCommand command) {
        String normalizedCode = Validations.requireText(code, "code");
        Membership membership = readMembershipPort.getByCode(normalizedCode)
                .orElseThrow(() -> DomainException.notFound("Membership not found: " + normalizedCode));

        MembershipStatus current = membership.status();
        MembershipStatus target = command.status();
        if (!current.canTransitionTo(target)) {
            throw DomainException.validation(
                    "Invalid membership status transition: " + current + " -> " + target
            );
        }

        if (target == MembershipStatus.ACTIVE) {
            PackagePlan packagePlan = readPackagePlanPort.findById(membership.packagePlanId())
                    .orElseThrow(() -> DomainException.notFound(
                            "Package plan not found for membership: " + membership.packagePlanId()
                    ));
            if (!packagePlan.active()) {
                throw DomainException.validation("Cannot activate membership for inactive package plan: " + packagePlan.code());
            }
        }

        // Guarded update: only transition if the row still holds the status we validated against.
        // 0 rows => a concurrent request already moved it, so this transition is no longer valid.
        int updated = writeMembershipPort.updateStatus(normalizedCode, current, target);
        if (updated == 0) {
            throw DomainException.conflict("Membership status changed concurrently, please retry: " + normalizedCode);
        }
        return assembler.toDetail(reload(normalizedCode));
    }

    private Membership reload(String code) {
        return readMembershipPort.getByCode(code)
                .orElseThrow(() -> DomainException.notFound("Membership could not be reloaded: " + code));
    }

    private static OffsetDateTime calculateEffectiveTo(OffsetDateTime effectiveFrom, Integer durationDays) {
        if (durationDays == null) {
            return null;
        }
        return effectiveFrom.plusDays(durationDays);
    }
}
