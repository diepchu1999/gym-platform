package com.gym.identity.application.service;

import com.gym.identity.api.UserAccountProvisioning;
import com.gym.identity.api.UserAccountDirectory;
import com.gym.identity.api.UserAccountRef;
import com.gym.identity.application.port.out.ReadUserAccountPort;
import com.gym.identity.application.port.out.WriteUserAccountPort;
import com.gym.shared.error.DomainException;
import com.gym.shared.validation.Validations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;
import java.util.Optional;

@Service
class UserAccountProvisioningService implements UserAccountProvisioning, UserAccountDirectory {
    private static final String ACCOUNT_TYPE_STAFF = "STAFF";

    private final ReadUserAccountPort readUserAccountPort;
    private final WriteUserAccountPort writeUserAccountPort;

    UserAccountProvisioningService(
            ReadUserAccountPort readUserAccountPort,
            WriteUserAccountPort writeUserAccountPort
    ) {
        this.readUserAccountPort = readUserAccountPort;
        this.writeUserAccountPort = writeUserAccountPort;
    }

    @Override
    @Transactional
    public UserAccountRef ensureStaffAccount(UUID keycloakUserId, String username, String email) {
        UUID requiredKeycloakUserId = Validations.requireNonNull(keycloakUserId, "keycloakUserId");
        UserAccountRef existing = readUserAccountPort.findByKeycloakUserId(requiredKeycloakUserId).orElse(null);
        if (existing != null) {
            if (!ACCOUNT_TYPE_STAFF.equals(existing.accountType())) {
                throw DomainException.conflict("Keycloak user is already linked to non-staff account");
            }
            return existing;
        }

        return writeUserAccountPort.insert(new WriteUserAccountPort.NewUserAccount(
                requiredKeycloakUserId,
                ACCOUNT_TYPE_STAFF,
                Validations.trimToNull(username),
                Validations.trimToNull(email)
        ));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UserAccountRef> findByKeycloakUserId(UUID keycloakUserId) {
        UUID requiredKeycloakUserId = Validations.requireNonNull(keycloakUserId, "keycloakUserId");
        return readUserAccountPort.findByKeycloakUserId(requiredKeycloakUserId);
    }
}
