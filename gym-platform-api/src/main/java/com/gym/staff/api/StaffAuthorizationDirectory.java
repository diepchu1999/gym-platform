package com.gym.staff.api;

import java.util.List;
import java.util.Optional;

public interface StaffAuthorizationDirectory {
    Optional<StaffPrincipalRef> findPrincipalByUserAccountId(long userAccountId);

    List<StaffAssignmentRef> listActiveAssignments(long staffId);
}
