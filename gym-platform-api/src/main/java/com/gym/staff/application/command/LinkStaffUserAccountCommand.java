package com.gym.staff.application.command;

import com.gym.shared.validation.Validations;

import java.util.UUID;

public record LinkStaffUserAccountCommand(
        UUID keycloakUserId,
        String username,
        String email
) {
    public static LinkStaffUserAccountCommand from(String keycloakUserId, String username, String email) {
        UUID parsedKeycloakUserId = Validations.requireNonNull(
                Validations.optionalUuid(keycloakUserId, "keycloakUserId"),
                "keycloakUserId"
        );
        return new LinkStaffUserAccountCommand(
                parsedKeycloakUserId,
                Validations.trimToNull(username),
                Validations.trimToNull(email)
        );
    }
}
