package com.gym.shared.error;

/**
 * Base cho lỗi nghiệp vụ. Mỗi lỗi mang một code dạng MEMBER_NOT_FOUND, OUT_OF_STOCK...
 * (xem backend-guideline.md). Application/domain ném ra; GlobalExceptionHandler map sang HTTP.
 */
public class BusinessException extends RuntimeException {

    private final String code;

    public BusinessException(String code, String message) {
        super(message);
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
