package com.gym.identity.api;

import java.util.UUID;

public interface UserAccountProvisioning {
    UserAccountRef ensureStaffAccount(UUID keycloakUserId, String username, String email);
}