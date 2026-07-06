package com.gym.membership.application.service;

import com.gym.branch.api.BranchDirectory;
import com.gym.branch.api.BranchRef;
import com.gym.member.api.MemberDirectory;
import com.gym.member.api.MemberRef;
import com.gym.membership.application.port.out.ReadPackagePlanPort;
import com.gym.membership.application.view.MembershipDetail;
import com.gym.membership.application.view.MembershipListItem;
import com.gym.membership.domain.Membership;
import com.gym.membership.domain.PackagePlan;
import com.gym.shared.error.DomainException;
import org.springframework.stereotype.Component;

@Component
class MembershipDetailAssembler {
    private final MemberDirectory memberDirectory;
    private final BranchDirectory branchDirectory;
    private final ReadPackagePlanPort readPackagePlanPort;

    MembershipDetailAssembler(
            MemberDirectory memberDirectory,
            BranchDirectory branchDirectory,
            ReadPackagePlanPort readPackagePlanPort
    ) {
        this.memberDirectory = memberDirectory;
        this.branchDirectory = branchDirectory;
        this.readPackagePlanPort = readPackagePlanPort;
    }

    MembershipDetail toDetail(Membership membership) {
        MemberRef member = memberRef(membership.memberId());
        BranchRef branch = branchRef(membership.saleBranchId());
        PackagePlan packagePlan = packagePlan(membership.packagePlanId());

        return new MembershipDetail(
                membership.code(),
                member.code(),
                member.fullName(),
                packagePlan.code(),
                packagePlan.name(),
                packagePlan.packageType(),
                branch.code(),
                branch.name(),
                membership.status(),
                membership.effectiveFrom(),
                membership.effectiveTo(),
                membership.createdAt(),
                membership.updatedAt()
        );
    }

    MembershipListItem toListItem(Membership membership) {
        BranchRef branch = branchRef(membership.saleBranchId());
        PackagePlan packagePlan = packagePlan(membership.packagePlanId());

        return new MembershipListItem(
                membership.code(),
                packagePlan.code(),
                packagePlan.name(),
                packagePlan.packageType(),
                branch.code(),
                branch.name(),
                membership.status(),
                membership.effectiveFrom(),
                membership.effectiveTo()
        );
    }

    private MemberRef memberRef(long memberId) {
        return memberDirectory.findRefById(memberId)
                .orElseThrow(() -> DomainException.notFound("Member not found for membership: " + memberId));
    }

    private BranchRef branchRef(long branchId) {
        return branchDirectory.findRefById(branchId)
                .orElseThrow(() -> DomainException.notFound("Sale branch not found for membership: " + branchId));
    }

    private PackagePlan packagePlan(long packagePlanId) {
        return readPackagePlanPort.findById(packagePlanId)
                .orElseThrow(() -> DomainException.notFound("Package plan not found for membership: " + packagePlanId));
    }
}
