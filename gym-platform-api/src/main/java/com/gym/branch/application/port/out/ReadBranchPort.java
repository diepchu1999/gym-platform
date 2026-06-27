package com.gym.branch.application.port.out;

import com.gym.branch.api.BranchRef;
import com.gym.branch.application.query.SearchBranchesQuery;
import com.gym.branch.application.view.BranchDetail;
import com.gym.branch.application.view.BranchListItem;
import com.gym.branch.application.view.BranchOption;
import com.gym.shared.api.PageResponse;

import java.util.List;
import java.util.Optional;

public interface ReadBranchPort {
    Optional<BranchDetail> getByCode(String code);

    PageResponse<BranchListItem> search(SearchBranchesQuery query);

    List<BranchOption> listActive();

    boolean existsById(long id);

    Optional<BranchRef> findRefById(long id);

    Optional<BranchRef> findRefByCode(String code);
}
