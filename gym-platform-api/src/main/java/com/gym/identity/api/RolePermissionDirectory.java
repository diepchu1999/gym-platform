package com.gym.identity.api;

public interface RolePermissionDirectory {
    boolean roleHasPermission(long roleId, String permissionCode);
}
