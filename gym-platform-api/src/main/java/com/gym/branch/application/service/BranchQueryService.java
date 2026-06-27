package com.gym.branch.application.service;

import com.gym.branch.api.BranchDirectory;
import com.gym.branch.api.BranchRef;
import com.gym.branch.application.port.in.GetBranchUseCase;
import com.gym.branch.application.port.in.ListBranchesUseCase;
import com.gym.branch.application.port.in.SearchBranchesUseCase;
import com.gym.branch.application.port.out.ReadBranchPort;
import com.gym.branch.application.query.SearchBranchesQuery;
import com.gym.branch.application.view.BranchDetail;
import com.gym.branch.application.view.BranchListItem;
import com.gym.branch.application.view.BranchOption;
import com.gym.shared.api.ListResponse;
import com.gym.shared.api.PageResponse;
import com.gym.shared.error.DomainException;
import com.gym.shared.validation.Validations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
class BranchQueryService implements SearchBranchesUseCase, GetBranchUseCase, ListBranchesUseCase, BranchDirectory {
    private final ReadBranchPort readBranchPort;

    BranchQueryService(ReadBranchPort readBranchPort) {
        this.readBranchPort = readBranchPort;
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<BranchListItem> handle(SearchBranchesQuery query) {
        return readBranchPort.search(query);
    }

    @Override
    @Transactional(readOnly = true)
    public BranchDetail handle(String code) {
        String normalizedCode = Validations.requireText(code, "code");
        return readBranchPort.getByCode(normalizedCode)
                .orElseThrow(() -> DomainException.notFound("Branch not found: " + normalizedCode));
    }

    @Override
    @Transactional(readOnly = true)
    public ListResponse<BranchOption> handle() {
        return ListResponse.of(readBranchPort.listActive());
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsById(long id) {
        return readBranchPort.existsById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<BranchRef> findRefById(long id) {
        return readBranchPort.findRefById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<BranchRef> findRefByCode(String code) {
        String normalizedCode = Validations.requireText(code, "code");
        return readBranchPort.findRefByCode(normalizedCode);
    }
}
