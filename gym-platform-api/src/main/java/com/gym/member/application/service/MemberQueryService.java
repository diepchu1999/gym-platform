package com.gym.member.application.service;

import com.gym.branch.api.BranchDirectory;
import com.gym.branch.api.BranchRef;
import com.gym.member.api.MemberDirectory;
import com.gym.member.api.MemberRef;
import com.gym.member.application.port.in.GetMemberUseCase;
import com.gym.member.application.port.in.SearchMembersUseCase;
import com.gym.member.application.port.out.ReadMemberPort;
import com.gym.member.application.query.SearchMembersCriteria;
import com.gym.member.application.query.SearchMembersQuery;
import com.gym.member.application.view.MemberDetail;
import com.gym.member.application.view.MemberListItem;
import com.gym.member.domain.Member;
import com.gym.shared.api.PageResponse;
import com.gym.shared.error.DomainException;
import com.gym.shared.validation.Validations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
class MemberQueryService implements GetMemberUseCase, SearchMembersUseCase, MemberDirectory {
    private final ReadMemberPort readMemberPort;
    private final BranchDirectory branchDirectory;

    MemberQueryService(ReadMemberPort readMemberPort, BranchDirectory branchDirectory) {
        this.readMemberPort = readMemberPort;
        this.branchDirectory = branchDirectory;
    }

    @Override
    @Transactional(readOnly = true)
    public MemberDetail handle(String code) {
        String normalizedCode = Validations.requireText(code, "code");
        Member member = readMemberPort.getByCode(normalizedCode)
                .orElseThrow(() -> DomainException.notFound("Member not found: " + normalizedCode));
        BranchRef branchRef = branchDirectory.findRefById(member.homeBranchId())
                .orElseThrow(() -> DomainException.notFound(
                        "Home branch not found for member: " + normalizedCode
                ));

        return MemberDetail.fromDomain(member, branchRef.code(), branchRef.name());
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<MemberListItem> handle(SearchMembersQuery query) {
        Long branchId = null;
        if (query.branchCode() != null) {
            BranchRef branch = branchDirectory.findRefByCode(query.branchCode())
                    .orElseThrow(() -> DomainException.validation(
                            "branchCode not found: " + query.branchCode()
                    ));
            branchId = branch.id();
        }

        return readMemberPort.search(SearchMembersCriteria.from(query, branchId));
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsById(long id) {
        return readMemberPort.existsById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<MemberRef> findRefById(long id) {
        return readMemberPort.findRefById(id);
    }
}
