package com.gym.security.api;

public interface BranchAuthorizationService {
    void requireBranchPermission(String keycloakUserId, String branchCode, SecurityPermission permission);

    void requireGlobalPermission(String keycloakUserId, SecurityPermission permission);
}
