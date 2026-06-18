package com.gym.shared.validation;

import com.gym.shared.error.DomainException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.format.DateTimeParseException;
import java.util.UUID;

/**
 * Generic validation/parsing helpers shared by every {@code *Command.from(...)}
 * across the server. Any invalid value raises {@link DomainException#validation}
 * (HTTP 400). Resource-specific rules (domain enums, field combinations) belong
 * in a {@code <Resource>CommandValidation} class; enum parsing uses {@link Enums}.
 */
public final class Validations {

    private Validations() {}

    // ---------- String ----------

    /**
     * Requires a non-blank string.
     *
     * @param value the value to check
     * @param field the field name, used in the error message
     * @return the trimmed value
     * @throws DomainException if the value is null or blank
     */
    public static String requireText(String value, String field) {
        if (isBlank(value)) {
            throw DomainException.validation(field + " is required");
        }
        return value.trim();
    }

    /**
     * Trims a value, mapping blank to {@code null} (so nullable columns receive
     * {@code null} instead of an empty string).
     *
     * @param value the value to normalize
     * @return the trimmed value, or {@code null} if null or blank
     */
    public static String trimToNull(String value) {
        if (value == null) return null;
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    /**
     * @param value the value to check
     * @return {@code true} if the value is null or blank
     */
    public static boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    // ---------- Object / id ----------

    /**
     * Requires a non-null value.
     *
     * @param value the value to check
     * @param field the field name, used in the error message
     * @param <T>   the value type
     * @return the value
     * @throws DomainException if the value is null
     */
    public static <T> T requireNonNull(T value, String field) {
        if (value == null) {
            throw DomainException.validation(field + " is required");
        }
        return value;
    }

    /**
     * Parses an optional UUID from a string.
     *
     * @param value the raw value
     * @param field the field name, used in the error message
     * @return the parsed UUID, or {@code null} if null or blank
     * @throws DomainException if the value is present but not a valid UUID
     */
    public static UUID optionalUuid(String value, String field) {
        String v = trimToNull(value);
        if (v == null) return null;
        try {
            return UUID.fromString(v);
        } catch (IllegalArgumentException e) {
            throw DomainException.validation(field + " is invalid: " + value);
        }
    }

    // ---------- Numbers ----------

    /**
     * Requires a strictly positive integer.
     *
     * @param value the value to check
     * @param field the field name, used in the error message
     * @return the value
     * @throws DomainException if the value is null or not greater than zero
     */
    public static int requirePositive(Integer value, String field) {
        if (value == null || value <= 0) {
            throw DomainException.validation(field + " must be > 0");
        }
        return value;
    }

    /**
     * Requires a non-negative integer.
     *
     * @param value the value to check
     * @param field the field name, used in the error message
     * @return the value
     * @throws DomainException if the value is null or negative
     */
    public static int requireNonNegative(Integer value, String field) {
        if (value == null) {
            throw DomainException.validation(field + " is required");
        }
        if (value < 0) {
            throw DomainException.validation(field + " must be >= 0");
        }
        return value;
    }

    /**
     * Validates an optional non-negative integer.
     *
     * @param value the value to check
     * @param field the field name, used in the error message
     * @return the value, or {@code null} if not provided
     * @throws DomainException if the value is present and negative
     */
    public static Integer optionalNonNegative(Integer value, String field) {
        if (value == null) return null;
        if (value < 0) {
            throw DomainException.validation(field + " must be >= 0");
        }
        return value;
    }

    /**
     * Validates an optional non-negative amount.
     *
     * @param value the amount to check
     * @param field the field name, used in the error message
     * @return the value, or {@code null} if not provided
     * @throws DomainException if the value is present and negative
     */
    public static BigDecimal optionalNonNegativeAmount(BigDecimal value, String field) {
        if (value == null) return null;
        if (value.signum() < 0) {
            throw DomainException.validation(field + " must be >= 0");
        }
        return value;
    }

    /**
     * Returns an amount, defaulting null to zero (matching DB {@code DEFAULT 0}).
     *
     * @param value the amount to check
     * @param field the field name, used in the error message
     * @return the value, or {@link BigDecimal#ZERO} if null
     * @throws DomainException if the value is present and negative
     */
    public static BigDecimal amountOrZero(BigDecimal value, String field) {
        if (value == null) return BigDecimal.ZERO;
        if (value.signum() < 0) {
            throw DomainException.validation(field + " must be >= 0");
        }
        return value;
    }

    // ---------- Phone (Vietnam: leading 0 + 9-10 digits) ----------

    /**
     * Requires a valid Vietnamese phone number.
     *
     * @param phone the raw phone value
     * @return the trimmed phone number
     * @throws DomainException if the phone is missing or malformed
     */
    public static String requirePhone(String phone) {
        String p = trimToNull(phone);
        if (p == null) {
            throw DomainException.validation("phone is required");
        }
        return validatePhoneFormat(p);
    }

    /**
     * Validates an optional Vietnamese phone number.
     *
     * @param phone the raw phone value
     * @return the trimmed phone number, or {@code null} if not provided
     * @throws DomainException if the phone is present and malformed
     */
    public static String optionalPhone(String phone) {
        String p = trimToNull(phone);
        if (p == null) return null;
        return validatePhoneFormat(p);
    }

    private static String validatePhoneFormat(String p) {
        if (!p.matches("^0\\d{9,10}$")) {
            throw DomainException.validation("phone is invalid");
        }
        return p;
    }

    // ---------- Date / DateTime (ISO) ----------

    /**
     * Requires a date in ISO {@code yyyy-MM-dd} format.
     *
     * @param value the raw date value
     * @param field the field name, used in the error message
     * @return the parsed date
     * @throws DomainException if the date is missing or malformed
     */
    public static LocalDate requireDate(String value, String field) {
        LocalDate date = optionalDate(value, field);
        if (date == null) {
            throw DomainException.validation(field + " is required");
        }
        return date;
    }

    /**
     * Parses an optional date in ISO {@code yyyy-MM-dd} format.
     *
     * @param value the raw date value
     * @param field the field name, used in the error message
     * @return the parsed date, or {@code null} if not provided
     * @throws DomainException if the value is present and malformed
     */
    public static LocalDate optionalDate(String value, String field) {
        String v = trimToNull(value);
        if (v == null) return null;
        try {
            return LocalDate.parse(v);
        } catch (DateTimeParseException e) {
            throw DomainException.validation(field + " is invalid (expected yyyy-MM-dd): " + value);
        }
    }

    /**
     * Requires an ISO date-time.
     *
     * @param value the raw date-time value
     * @param field the field name, used in the error message
     * @return the parsed date-time
     * @throws DomainException if the date-time is missing or malformed
     */
    public static OffsetDateTime requireDateTime(String value, String field) {
        OffsetDateTime dt = optionalDateTime(value, field);
        if (dt == null) {
            throw DomainException.validation(field + " is required");
        }
        return dt;
    }

    /**
     * Parses an optional ISO date-time.
     *
     * @param value the raw date-time value
     * @param field the field name, used in the error message
     * @return the parsed date-time, or {@code null} if not provided
     * @throws DomainException if the value is present and malformed
     */
    public static OffsetDateTime optionalDateTime(String value, String field) {
        String v = trimToNull(value);
        if (v == null) return null;
        try {
            return OffsetDateTime.parse(v);
        } catch (DateTimeParseException e) {
            throw DomainException.validation(field + " is invalid (expected ISO datetime): " + value);
        }
    }
}
