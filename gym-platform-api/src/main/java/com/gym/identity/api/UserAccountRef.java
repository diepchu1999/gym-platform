package com.gym.identity.api;

import java.util.UUID;

public record UserAccountRef(
        long id,
        UUID keycloakUserId,
        String accountType,
        String username,
        String email,
        String status
) {
}
