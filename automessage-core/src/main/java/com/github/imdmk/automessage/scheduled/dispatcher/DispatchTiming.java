package com.github.imdmk.automessage.scheduled.dispatcher;

import com.github.imdmk.automessage.platform.logger.PluginLogger;
import com.github.imdmk.automessage.notice.time.DurationFormatter;
import com.github.imdmk.automessage.scheduled.channel.AnnouncementChannel;

import java.time.Duration;

public record DispatchTiming(
        Duration initialDelay,
        Duration period
) {

    public static final Duration MINIMUM_PERIOD = Duration.ofMillis(50L);

    public static final Duration DEFAULT_PERIOD = Duration.ofSeconds(10L);

    public DispatchTiming {
        if (initialDelay.isNegative()) {
            throw new IllegalArgumentException("initialDelay must not be negative");
        }

        if (period.compareTo(MINIMUM_PERIOD) < 0) {
            throw new IllegalArgumentException("period must be at least " + DurationFormatter.format(MINIMUM_PERIOD));
        }
    }

    public static DispatchTiming from(AnnouncementChannel channel, PluginLogger logger) {
        return new DispatchTiming(
                normalizeInitialDelay(channel.name(), channel.initialDelay(), logger),
                normalizePeriod(channel.name(), channel.period(), logger)
        );
    }

    private static Duration normalizeInitialDelay(String channel, Duration initialDelay, PluginLogger logger) {
        if (initialDelay == null) {
            logger.warn("Channel '%s' has no 'initialDelay', using 0s.", channel);
            return Duration.ZERO;
        }

        if (initialDelay.isNegative()) {
            logger.warn(
                    "Channel '%s' has a negative 'initialDelay' (%s), using 0s.",
                    channel,
                    DurationFormatter.format(initialDelay)
            );
            return Duration.ZERO;
        }

        return initialDelay;
    }

    private static Duration normalizePeriod(String channel, Duration period, PluginLogger logger) {
        if (period == null || period.isZero() || period.isNegative()) {
            logger.warn(
                    "Channel '%s' has an invalid 'period' (%s), using the default of %s. "
                            + "A plain number means seconds, so 'period: 30' is 30 seconds.",
                    channel,
                    DurationFormatter.format(period),
                    DurationFormatter.format(DEFAULT_PERIOD)
            );
            return DEFAULT_PERIOD;
        }

        if (period.compareTo(MINIMUM_PERIOD) < 0) {
            logger.warn(
                    "Channel '%s' has a 'period' (%s) shorter than a single server tick, using %s instead.",
                    channel,
                    DurationFormatter.format(period),
                    DurationFormatter.format(MINIMUM_PERIOD)
            );
            return MINIMUM_PERIOD;
        }

        return period;
    }
}
