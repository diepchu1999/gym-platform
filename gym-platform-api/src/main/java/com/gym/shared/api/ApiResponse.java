package com.gym.shared.api;

/**
 * Envelope chuẩn cho mọi response — không expose row DB trực tiếp (CLAUDE.md).
 */
public record ApiResponse<T>(boolean success, T data, ApiError error) {

    public static <T> ApiResponse<T> ok(T data) {
        return new ApiResponse<>(true, data, null);
    }

    public static ApiResponse<Void> fail(ApiError error) {
        return new ApiResponse<>(false, null, error);
    }
}
