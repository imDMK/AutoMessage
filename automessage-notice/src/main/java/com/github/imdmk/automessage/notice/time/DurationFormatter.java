package com.github.imdmk.automessage.notice.time;

import java.time.Duration;

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
