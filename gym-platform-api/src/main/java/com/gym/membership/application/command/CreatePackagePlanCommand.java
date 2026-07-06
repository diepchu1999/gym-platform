package com.gym.membership.application.command;

import com.gym.membership.domain.PackageType;
import com.gym.shared.validation.Enums;
import com.gym.shared.validation.Validations;

import java.math.BigDecimal;

public record CreatePackagePlanCommand(
        String code,
        String name,
        PackageType packageType,
        Integer durationDays,
        BigDecimal price,
        String currency,
        boolean vip,
        boolean studentOnly,
        Integer totalSessions,
        Integer dailyCheckinLimit,
        Integer privateRoomMinutesPerMonth,
        Integer massageFreePerWeek,
        boolean installmentAllowed,
        boolean active
) {
    public static CreatePackagePlanCommand from(
            String code,
            String name,
            String packageType,
            Integer durationDays,
            BigDecimal price,
            String currency,
            Boolean vip,
            Boolean studentOnly,
            Integer totalSessions,
            Integer dailyCheckinLimit,
            Integer privateRoomMinutesPerMonth,
            Integer massageFreePerWeek,
            Boolean installmentAllowed,
            Boolean active
    ) {
        PackageType parsedType = Enums.requireStrict(PackageType.class, "packageType", packageType);
        boolean parsedInstallmentAllowed = PackagePlanCommandValidation.booleanOrFalse(installmentAllowed);
        PackagePlanCommandValidation.validateInstallmentAllowed(parsedType, parsedInstallmentAllowed);

        return new CreatePackagePlanCommand(
                Validations.requireText(code, "code"),
                Validations.requireText(name, "name"),
                parsedType,
                Validations.optionalNonNegative(durationDays, "durationDays"),
                PackagePlanCommandValidation.requireNonNegativeAmount(price, "price"),
                PackagePlanCommandValidation.normalizeCurrency(currency),
                PackagePlanCommandValidation.booleanOrFalse(vip),
                PackagePlanCommandValidation.booleanOrFalse(studentOnly),
                Validations.optionalNonNegative(totalSessions, "totalSessions"),
                Validations.optionalNonNegative(dailyCheckinLimit, "dailyCheckinLimit"),
                Validations.optionalNonNegative(privateRoomMinutesPerMonth, "privateRoomMinutesPerMonth"),
                Validations.optionalNonNegative(massageFreePerWeek, "massageFreePerWeek"),
                parsedInstallmentAllowed,
                PackagePlanCommandValidation.booleanOrTrue(active)
        );
    }
}
