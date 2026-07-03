package com.gym.identity.api;

import java.util.Optional;

public interface RoleDirectory {
    Optional<RoleRef> findRefByCode(String code);

    Optional<RoleRef> findRefById(long id);
}
