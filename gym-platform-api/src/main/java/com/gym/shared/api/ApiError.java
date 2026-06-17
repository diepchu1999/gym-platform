package com.gym.shared.api;

/**
 * Lỗi nghiệp vụ chuẩn: code (vd MEMBER_NOT_FOUND) + message.
 */
public record ApiError(String code, String message) {
}
