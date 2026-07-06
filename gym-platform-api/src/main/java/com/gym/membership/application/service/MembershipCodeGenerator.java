package com.gym.membership.application.service;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ThreadLocalRandom;

final class MembershipCodeGenerator {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    private MembershipCodeGenerator() {
    }

    static String next() {
        int suffix = ThreadLocalRandom.current().nextInt(1000, 10_000);
        return "MBS-" + OffsetDateTime.now().format(FORMATTER) + "-" + suffix;
    }
}
