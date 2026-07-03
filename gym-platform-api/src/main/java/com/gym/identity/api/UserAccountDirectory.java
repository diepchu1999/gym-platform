package com.gym.identity.api;

import java.util.Optional;
import java.util.UUID;

public interface UserAccountDirectory {
    Optional<UserAccountRef> findByKeycloakUserId(UUID keycloakUserId);
}
