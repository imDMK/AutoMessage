package com.github.imdmk.automessage.notice.time;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

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

    /**
     * The same duration written for somebody to read rather than for a configuration file.
     */
    // Whole seconds, because the milliseconds in "34s572ms" are noise on a line a person reads,
    // and spaced, because "1h 34s" is read at a glance where "1h34s" has to be picked apart.
    // Anything left under a second still reads as "1s": a countdown that says "0s" while there
    // is time left is worse than one that rounds.
    public static String formatReadable(Duration duration) {
        if (duration == null) {
            return UNSET;
        }

        final Duration whole = duration.truncatedTo(ChronoUnit.SECONDS);

        if (whole.isZero() && !duration.isZero()) {
            return format(Duration.ofSeconds(duration.isNegative() ? -1L : 1L));
        }

        return space(format(whole));
    }

    private static String space(String formatted) {
        return formatted.replaceAll("(?<=[a-z])(?=\\d)", " ");
    }
}
