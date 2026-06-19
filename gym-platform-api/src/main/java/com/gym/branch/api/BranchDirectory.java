package com.gym.branch.api;

import java.util.Optional;

public interface BranchDirectory {
    boolean existsById(long id);

    Optional<BranchRef> findRefById(long id);
}
