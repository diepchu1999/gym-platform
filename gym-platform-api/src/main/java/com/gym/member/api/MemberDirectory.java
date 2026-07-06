package com.gym.member.api;

import java.util.Optional;

public interface MemberDirectory {
    boolean existsById(long id);

    Optional<MemberRef> findRefById(long id);

    Optional<MemberRef> findRefByCode(String code);
}
