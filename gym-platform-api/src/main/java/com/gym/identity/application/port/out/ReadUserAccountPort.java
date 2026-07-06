package com.gym.identity.application.port.out;

import com.gym.identity.api.UserAccountRef;

import java.util.Optional;
import java.util.UUID;

public interface ReadUserAccountPort {
    Optional<UserAccountRef> findByKeycloakUserId(UUID keycloakUserId);
}
