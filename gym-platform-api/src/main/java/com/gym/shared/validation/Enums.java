package com.gym.shared.validation;

import com.gym.shared.error.DomainException;
import java.util.Arrays;

/**
 * Shared enum parsing for queries and commands across the server. Strict by
 * design: an invalid value raises VALIDATION_ERROR (HTTP 400) rather than being
 * silently dropped, so callers get a clear contract.
 */
public final class Enums {

    private Enums() {}

    /**
     * Parses an optional enum value: null or blank yields {@code null} (useful
     * for filter queries and optional fields).
     *
     * @param type      the enum type
     * @param paramName the parameter name, used in the error message
     * @param value     the raw value (case-insensitive, trimmed)
     * @param <E>       the enum type
     * @return the parsed constant, or {@code null} if the value is null/blank
     * @throws DomainException if the value is present but not a valid constant
     */
    public static <E extends Enum<E>> E parseStrict(Class<E> type, String paramName, String value) {
        if (value == null || value.isBlank()) return null;
        try {
            return Enum.valueOf(type, value.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw DomainException.validation(String.format(
                    "Invalid value '%s' for parameter '%s'. Allowed values: %s",
                    value, paramName, Arrays.toString(type.getEnumConstants())));
        }
    }

    /**
     * Parses a required enum value: null or blank yields a 400.
     *
     * @param type      the enum type
     * @param paramName the parameter name, used in the error message
     * @param value     the raw value (case-insensitive, trimmed)
     * @param <E>       the enum type
     * @return the parsed constant
     * @throws DomainException if the value is missing or not a valid constant
     */
    public static <E extends Enum<E>> E requireStrict(Class<E> type, String paramName, String value) {
        E parsed = parseStrict(type, paramName, value);
        if (parsed == null) {
            throw DomainException.validation(paramName + " is required");
        }
        return parsed;
    }
}
