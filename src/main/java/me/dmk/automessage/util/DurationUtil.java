package me.dmk.automessage.util;

import java.time.Duration;

public final class DurationUtil {

    private DurationUtil() {
        throw new UnsupportedOperationException("This is utility class.");
    }

    public static long toTicks(Duration duration) {
        return duration.dividedBy(50L).toMillis(); //50 Milliseconds = 1 tick
    }
}
