package com.gym.membership.application.command;

import com.gym.membership.domain.PackageType;
import com.gym.shared.error.DomainException;
import com.gym.shared.validation.Validations;

import java.math.BigDecimal;
import java.util.Locale;

final class PackagePlanCommandValidation {
    private PackagePlanCommandValidation() {
    }

    static BigDecimal requireNonNegativeAmount(BigDecimal value, String field) {
        BigDecimal amount = Validations.requireNonNull(value, field);
        if (amount.signum() < 0) {
            throw DomainException.validation(field + " must be >= 0");
        }
        return amount;
    }

    static String normalizeCurrency(String value) {
        String currency = Validations.requireText(value, "currency").toUpperCase(Locale.ROOT);
        if (!currency.matches("^[A-Z]{3}$")) {
            throw DomainException.validation("currency must be a 3-letter ISO code");
        }
        return currency;
    }

    static boolean booleanOrFalse(Boolean value) {
        return Boolean.TRUE.equals(value);
    }

    static boolean booleanOrTrue(Boolean value) {
        return value == null || value;
    }

    static void validateInstallmentAllowed(PackageType packageType, boolean installmentAllowed) {
        if (!installmentAllowed) {
            return;
        }
        if (packageType != PackageType.QUARTERLY && packageType != PackageType.YEARLY) {
            throw DomainException.validation("installmentAllowed is only valid for QUARTERLY or YEARLY packages");
        }
    }
}
