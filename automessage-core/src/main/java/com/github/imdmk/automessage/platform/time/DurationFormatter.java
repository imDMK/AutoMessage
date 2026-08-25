package com.github.imdmk.automessage.platform.time;

import java.time.Duration;

/**
 * Formats {@link Duration} values using the same notation the configuration files accept,
 * for example {@code 10s}, {@code 1m30s} or {@code 500ms}.
 *
 * <p>
 * {@link Duration#toString()} produces ISO-8601 output such as {@code PT1M30S}, which does not
 * match what an administrator sees in the YAML files. Logging the configuration back in its own
 * notation keeps console output and config files consistent.
 * </p>
 */
public final class DurationFormatter {

    private static final String UNSET = "unset";

    private DurationFormatter() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated.");
    }

    public static String format(Duration duration) {
        if (duration == null) {
            return UNSET;
        }

        if (duration.isZero()) {
            return "0s";
        }

        final StringBuilder builder = new StringBuilder();
        Duration value = duration;

        if (value.isNegative()) {
            builder.append('-');
            value = value.negated();
        }

        appendPart(builder, value.toHours(), "h");
        appendPart(builder, value.toMinutesPart(), "m");
        appendPart(builder, value.toSecondsPart(), "s");
        appendPart(builder, value.toMillisPart(), "ms");

        return builder.toString();
    }

    private static void appendPart(StringBuilder builder, long amount, String unit) {
        if (amount > 0) {
            builder.append(amount).append(unit);
        }
    }
}
