package com.gym.shared.api;

public final class QueryParams {
    private QueryParams() {
    }

    public static String filterOrNull(String value) {
        if (value == null) return null;
        String v = value.trim();
        return (v.isEmpty() || v.equalsIgnoreCase("all")) ? null : v;
    }

    public static String searchOrEmpty(String value) {
        return value == null ? "" : value.trim();
    }
}
