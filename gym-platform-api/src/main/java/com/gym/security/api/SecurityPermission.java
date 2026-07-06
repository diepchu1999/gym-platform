package com.gym.security.api;

public enum SecurityPermission {
    MEMBER_CREATE("MEMBER_CREATE"),
    STAFF_MANAGE("STAFF_MANAGE"),
    RBAC_MANAGE("RBAC_MANAGE"),
    PACKAGE_MANAGE("PACKAGE_MANAGE"),
    PACKAGE_SELL("PACKAGE_SELL");

    private final String code;

    SecurityPermission(String code) {
        this.code = code;
    }

    public String code() {
        return code;
    }
}
