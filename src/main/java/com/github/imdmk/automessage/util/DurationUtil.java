package com.github.imdmk.automessage.util;

import java.time.Duration;

public final class DurationUtil {

    private DurationUtil() {
        throw new UnsupportedOperationException("This is utility class.");
    }

    public static String toHumanReadable(Duration duration) {
        return Duration.ofSeconds(duration.toSeconds())
                .toString()
                .substring(2)
                .replaceAll("(\\d[HMS])(?!$)", "$1 ")
                .toLowerCase();
    }

    public static long toTicks(Duration duration) {
        return duration.dividedBy(50L).toMillis(); //50 Milliseconds = 1 tick
    }
}
