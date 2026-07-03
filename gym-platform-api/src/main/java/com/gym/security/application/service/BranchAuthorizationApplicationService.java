package com.gym.security.application.service;

import com.gym.branch.api.BranchDirectory;
import com.gym.branch.api.BranchRef;
import com.gym.identity.api.RolePermissionDirectory;
import com.gym.identity.api.UserAccountDirectory;
import com.gym.identity.api.UserAccountRef;
import com.gym.security.api.BranchAuthorizationService;
import com.gym.security.api.SecurityPermission;
import com.gym.shared.error.DomainException;
import com.gym.staff.api.StaffAssignmentRef;
import com.gym.staff.api.StaffAuthorizationDirectory;
import com.gym.staff.api.StaffPrincipalRef;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
class BranchAuthorizationApplicationService implements BranchAuthorizationService {
    private final UserAccountDirectory userAccountDirectory;
    private final StaffAuthorizationDirectory staffAuthorizationDirectory;
    private final RolePermissionDirectory rolePermissionDirectory;
    private final BranchDirectory branchDirectory;

    BranchAuthorizationApplicationService(
            UserAccountDirectory userAccountDirectory,
            StaffAuthorizationDirectory staffAuthorizationDirectory,
            RolePermissionDirectory rolePermissionDirectory,
            BranchDirectory branchDirectory
    ) {
        this.userAccountDirectory = userAccountDirectory;
        this.staffAuthorizationDirectory = staffAuthorizationDirectory;
        this.rolePermissionDirectory = rolePermissionDirectory;
        this.branchDirectory = branchDirectory;
    }

    @Override
    @Transactional(readOnly = true)
    public void requireBranchPermission(String keycloakUserId, String branchCode, SecurityPermission permission) {
        StaffPrincipalRef staff = resolveStaffPrincipal(keycloakUserId);
        BranchRef branch = branchDirectory.findRefByCode(requireText(branchCode, "branchCode"))
                .orElseThrow(() -> DomainException.validation("branchCode not found: " + branchCode));

        List<StaffAssignmentRef> assignments = staffAuthorizationDirectory.listActiveAssignments(staff.id());
        boolean allowed = assignments.stream()
                .filter(assignment -> appliesToBranch(assignment, branch.id()))
                .anyMatch(assignment -> rolePermissionDirectory.roleHasPermission(
                        assignment.roleId(),
                        permission.code()
                ));

        if (!allowed) {
            throw DomainException.forbidden(
                    "Missing permission " + permission.code() + " for branch " + branch.code()
            );
        }
    }

    @Override
    @Transactional(readOnly = true)
    public void requireGlobalPermission(String keycloakUserId, SecurityPermission permission) {
        StaffPrincipalRef staff = resolveStaffPrincipal(keycloakUserId);

        boolean allowed = staffAuthorizationDirectory.listActiveAssignments(staff.id()).stream()
                .filter(assignment -> assignment.branchId() == null)
                .anyMatch(assignment -> rolePermissionDirectory.roleHasPermission(
                        assignment.roleId(),
                        permission.code()
                ));

        if (!allowed) {
            throw DomainException.forbidden("Missing global permission " + permission.code());
        }
    }

    private StaffPrincipalRef resolveStaffPrincipal(String keycloakUserId) {
        UUID parsedKeycloakUserId = parseKeycloakUserId(keycloakUserId);
        UserAccountRef account = userAccountDirectory.findByKeycloakUserId(parsedKeycloakUserId)
                .orElseThrow(() -> DomainException.forbidden("User account is not provisioned"));

        return staffAuthorizationDirectory.findPrincipalByUserAccountId(account.id())
                .orElseThrow(() -> DomainException.forbidden("User account is not linked to staff"));
    }

    private static UUID parseKeycloakUserId(String keycloakUserId) {
        String required = requireText(keycloakUserId, "keycloakUserId");
        try {
            return UUID.fromString(required);
        } catch (IllegalArgumentException e) {
            throw DomainException.forbidden("Authenticated principal is not a valid Keycloak user id");
        }
    }

    private static String requireText(String value, String field) {
        if (value == null || value.isBlank()) {
            throw DomainException.forbidden(field + " is required");
        }
        return value.trim();
    }

    private static boolean appliesToBranch(StaffAssignmentRef assignment, long branchId) {
        return assignment.branchId() == null || assignment.branchId() == branchId;
    }
}
