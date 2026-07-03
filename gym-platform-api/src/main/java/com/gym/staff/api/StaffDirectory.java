package com.gym.staff.api;

import java.util.Optional;

public interface StaffDirectory {
    boolean existsById(long id);

    Optional<StaffRef> findRefById(long id);
}
