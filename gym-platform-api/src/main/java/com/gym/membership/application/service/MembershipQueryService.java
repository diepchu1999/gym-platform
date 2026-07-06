package com.gym.membership.application.service;

import com.gym.member.api.MemberDirectory;
import com.gym.member.api.MemberRef;
import com.gym.membership.api.MembershipDirectory;
import com.gym.membership.api.MembershipRef;
import com.gym.membership.application.port.in.GetMembershipUseCase;
import com.gym.membership.application.port.in.ListMemberMembershipsUseCase;
import com.gym.membership.application.port.out.ReadMembershipPort;
import com.gym.membership.application.query.ListMemberMembershipsQuery;
import com.gym.membership.application.view.MembershipDetail;
import com.gym.membership.application.view.MembershipListItem;
import com.gym.membership.domain.Membership;
import com.gym.shared.api.ListResponse;
import com.gym.shared.error.DomainException;
import com.gym.shared.validation.Validations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
class MembershipQueryService implements GetMembershipUseCase, ListMemberMembershipsUseCase, MembershipDirectory {
    private final ReadMembershipPort readMembershipPort;
    private final MemberDirectory memberDirectory;
    private final MembershipDetailAssembler assembler;

    MembershipQueryService(
            ReadMembershipPort readMembershipPort,
            MemberDirectory memberDirectory,
            MembershipDetailAssembler assembler
    ) {
        this.readMembershipPort = readMembershipPort;
        this.memberDirectory = memberDirectory;
        this.assembler = assembler;
    }

    @Override
    @Transactional(readOnly = true)
    public MembershipDetail handle(String code) {
        String normalizedCode = Validations.requireText(code, "code");
        Membership membership = readMembershipPort.getByCode(normalizedCode)
                .orElseThrow(() -> DomainException.notFound("Membership not found: " + normalizedCode));
        return assembler.toDetail(membership);
    }

    @Override
    @Transactional(readOnly = true)
    public ListResponse<MembershipListItem> handle(ListMemberMembershipsQuery query) {
        MemberRef member = memberDirectory.findRefByCode(query.memberCode())
                .orElseThrow(() -> DomainException.notFound("Member not found: " + query.memberCode()));
        return ListResponse.of(readMembershipPort.listByMemberId(member.id()).stream()
                .map(assembler::toListItem)
                .toList());
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasActiveMembership(long memberId) {
        return readMembershipPort.hasActiveMembership(memberId);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<MembershipRef> findActiveByMember(long memberId) {
        return readMembershipPort.findActiveByMember(memberId);
    }
}
