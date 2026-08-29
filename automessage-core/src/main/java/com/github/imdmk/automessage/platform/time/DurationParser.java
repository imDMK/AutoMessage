package com.github.imdmk.automessage.platform.time;

import java.time.Duration;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Reads the same duration syntax the dispatcher configuration documents: {@code 500ms}, {@code
 * 30s}, {@code 5m}, {@code 2h}, {@code 1d}, and combinations such as {@code 1h30m}.
 *
 * <p>
 * The dispatcher gets this syntax from okaeri's {@code @DurationSpec}, which only applies to
 * mapped config fields. Rules are written by a hand-rolled serializer, so they need their own
 * reader — but not their own syntax: an administrator should never have to remember that one part
 * of the file counts minutes differently from another.
 * </p>
 */
public final class DurationParser {

    private static final Pattern PART = Pattern.compile("(\\d+)(ms|s|m|h|d)");
    private static final Pattern BARE_NUMBER = Pattern.compile("\\d+");

    private DurationParser() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated.");
    }

    /**
     * @param value duration such as {@code 1h30m}
     * @return the parsed duration
     * @throws IllegalArgumentException when the value is blank or not a duration
     */
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
