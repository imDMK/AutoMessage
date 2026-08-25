package com.github.imdmk.automessage.scheduled.dispatcher;

import com.github.imdmk.automessage.platform.logger.PluginLogger;
import com.github.imdmk.automessage.platform.time.DurationFormatter;

import java.time.Duration;

/**
 * Validated timing of the automatic message dispatcher.
 *
 * <p>
 * Values coming from {@code messagesDispatcher.yml} are user supplied and may be missing,
 * negative or shorter than a single server tick. They are normalized once, when the dispatcher
 * task is scheduled, so the scheduler never receives a value it cannot honour.
 * </p>
 *
 * <p>
 * A period shorter than one tick is silently rounded down to zero ticks by Bukkit, which makes
 * the dispatcher fire on every tick instead of on the configured interval.
 * </p>
 *
 * @param initialDelay delay before the first dispatch, never negative
 * @param period       delay between two dispatches, never shorter than {@link #MINIMUM_PERIOD}
 */
public record DispatchTiming(
        Duration initialDelay,
        Duration period
) {

    /** One server tick — the shortest interval Bukkit is able to schedule. */
    public static final Duration MINIMUM_PERIOD = Duration.ofMillis(50L);

    /** Period used when the configured one is missing or not a positive value. */
    public static final Duration DEFAULT_PERIOD = Duration.ofSeconds(10L);

    public DispatchTiming {
        if (initialDelay.isNegative()) {
            throw new IllegalArgumentException("initialDelay must not be negative");
        }

        if (period.compareTo(MINIMUM_PERIOD) < 0) {
            throw new IllegalArgumentException("period must be at least " + DurationFormatter.format(MINIMUM_PERIOD));
        }
    }

    /**
     * Reads the timing from the configuration, replacing every value the scheduler could not
     * honour with a safe one and reporting each correction to the console.
     *
     * @param config dispatcher configuration to read from
     * @param logger logger used to report corrected values
     * @return normalized, always schedulable timing
     */
    public static DispatchTiming from(MessageDispatcherConfig config, PluginLogger logger) {
        return new DispatchTiming(
                normalizeInitialDelay(config.initialDelay, logger),
                normalizePeriod(config.period, logger)
        );
    }

    private static Duration normalizeInitialDelay(Duration initialDelay, PluginLogger logger) {
        if (initialDelay == null) {
            logger.warn("Missing 'initialDelay' in messagesDispatcher.yml, using 0s.");
            return Duration.ZERO;
        }

        if (initialDelay.isNegative()) {
            logger.warn(
                    "Negative 'initialDelay' (%s) in messagesDispatcher.yml, using 0s.",
                    DurationFormatter.format(initialDelay)
            );
            return Duration.ZERO;
        }

        return initialDelay;
    }

    private static Duration normalizePeriod(Duration period, PluginLogger logger) {
        if (period == null || period.isZero() || period.isNegative()) {
            logger.warn(
                    "Invalid 'period' (%s) in messagesDispatcher.yml, using the default of %s. "
                            + "A plain number means seconds, so 'period: 30' is 30 seconds.",
                    DurationFormatter.format(period),
                    DurationFormatter.format(DEFAULT_PERIOD)
            );
            return DEFAULT_PERIOD;
        }

        if (period.compareTo(MINIMUM_PERIOD) < 0) {
            logger.warn(
                    "The configured 'period' (%s) is shorter than a single server tick, using %s instead.",
                    DurationFormatter.format(period),
                    DurationFormatter.format(MINIMUM_PERIOD)
            );
            return MINIMUM_PERIOD;
        }

        return period;
    }
}
