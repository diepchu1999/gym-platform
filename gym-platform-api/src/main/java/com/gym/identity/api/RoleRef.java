package com.gym.identity.api;

public record RoleRef(
        long id,
        String code,
        String name,
        String scope
) {
}
