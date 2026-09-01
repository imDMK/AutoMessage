package com.github.imdmk.automessage.notice.time;

import java.time.Duration;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class DurationParser {

    private static final Pattern PART = Pattern.compile("(\\d+)(ms|s|m|h|d)");
    private static final Pattern BARE_NUMBER = Pattern.compile("\\d+");

    private DurationParser() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated.");
    }

    public static Duration parse(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("duration must not be blank");
        }

        final String normalized = value.trim().toLowerCase(Locale.ROOT);

        // Matches how 'period: 10' is read in messagesDispatcher.yml.
        if (BARE_NUMBER.matcher(normalized).matches()) {
            return Duration.ofSeconds(Long.parseLong(normalized));
        }

        final Matcher matcher = PART.matcher(normalized);

        Duration total = Duration.ZERO;
        int consumed = 0;

        while (matcher.find()) {
            if (matcher.start() != consumed) {
                throw new IllegalArgumentException("Unrecognised duration: " + value);
            }

            total = total.plus(toDuration(Long.parseLong(matcher.group(1)), matcher.group(2)));
            consumed = matcher.end();
        }

        if (consumed != normalized.length() || total.isZero() && consumed == 0) {
            throw new IllegalArgumentException("Unrecognised duration: " + value);
        }

        return total;
    }

    private static Duration toDuration(long amount, String unit) {
        return switch (unit) {
            case "ms" -> Duration.ofMillis(amount);
            case "s" -> Duration.ofSeconds(amount);
            case "m" -> Duration.ofMinutes(amount);
            case "h" -> Duration.ofHours(amount);
            case "d" -> Duration.ofDays(amount);
            default -> throw new IllegalArgumentException("Unknown duration unit: " + unit);
        };
    }
}
