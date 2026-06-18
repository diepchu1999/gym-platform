package com.gym.shared.error;

public class DomainException extends RuntimeException {
    private final ErrorCode errorCode;

    public DomainException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public ErrorCode errorCode() {
        return errorCode;
    }

    public static DomainException notFound(String message) {
        return new DomainException(ErrorCode.NOT_FOUND, message);
    }

    public static DomainException validation(String message) {
        return new DomainException(ErrorCode.VALIDATION_ERROR, message);
    }

    public static DomainException conflict(String message) {
        return new DomainException(ErrorCode.CONFLICT, message);
    }

    public static DomainException forbidden(String message) {
        return new DomainException(ErrorCode.FORBIDDEN, message);
    }

    public static DomainException unauthorized(String message) {
        return new DomainException(ErrorCode.UNAUTHORIZED, message);
    }


}
