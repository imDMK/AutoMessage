package com.github.imdmk.automessage.util;

import dev.rollczi.litecommands.time.DurationParser;
import dev.rollczi.litecommands.time.TemporalAmountParser;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

/**
 * Utility class for parsing and formatting {@link Duration} values.
 */
public final class DurationUtil {

    private static final TemporalAmountParser<Duration> DATE_TIME_PARSER = new DurationParser()
            .withUnit("s", ChronoUnit.SECONDS)
            .withUnit("m", ChronoUnit.MINUTES)
            .withUnit("h", ChronoUnit.HOURS)
            .withUnit("d", ChronoUnit.DAYS)
            .withUnit("w", ChronoUnit.WEEKS)
            .withUnit("mo", ChronoUnit.MONTHS)
            .withUnit("y", ChronoUnit.YEARS);

    private DurationUtil() {
        throw new UnsupportedOperationException("This is utility class.");
    }

    /**
     * Formats a given duration into a human-readable string.
     *
     * @param duration the duration to format
     * @return a formatted string or "&lt;1s" if duration is zero or negative
     */
    @NotNull
    public static String format(@NotNull final Duration duration) {
        if (duration.isZero() || duration.isNegative()) {
            return "<1s";
        }

        return DATE_TIME_PARSER.format(duration);
    }

    /**
     * Converts a duration to Minecraft ticks (1 tick = 50ms).
     *
     * @param duration the duration to convert
     * @return duration in ticks
     */
    public static long toTicks(@NotNull final Duration duration) {
        if (duration.isZero() || duration.isNegative()) {
            return 0L;
        }

        return duration.toMillis() / 50;
    }

    /**
     * Converts Minecraft ticks to a {@link Duration}.
     * 1 tick = 50 milliseconds.
     *
     * @param ticks the number of ticks to convert
     * @return a {@link Duration} representing the given number of ticks
     */
    @NotNull
    public static Duration fromTicks(final long ticks) {
        if (ticks <= 0) {
            return Duration.ZERO;
        }

        return Duration.ofMillis(ticks * 50L);
    }

    @NotNull
    public static TemporalAmountParser<Duration> parser() {
        return DATE_TIME_PARSER;
    }
}
