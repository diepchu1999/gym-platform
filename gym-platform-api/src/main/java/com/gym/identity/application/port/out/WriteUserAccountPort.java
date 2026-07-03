package com.gym.identity.application.port.out;

import com.gym.identity.api.UserAccountRef;

import java.util.UUID;

public interface WriteUserAccountPort {
    UserAccountRef insert(NewUserAccount account);

    record NewUserAccount(
            UUID keycloakUserId,
            String accountType,
            String username,
            String email
    ) {
    }
}
