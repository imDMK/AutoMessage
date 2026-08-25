package com.github.imdmk.automessage.scheduled.dispatcher;

import com.github.imdmk.automessage.platform.logger.PluginLogger;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockingDetails;
import static org.mockito.Mockito.verifyNoInteractions;

class DispatchTimingTest {

    private final PluginLogger logger = mock(PluginLogger.class);

    private static MessageDispatcherConfig config(Duration initialDelay, Duration period) {
        MessageDispatcherConfig config = new MessageDispatcherConfig();
        config.initialDelay = initialDelay;
        config.period = period;
        return config;
    }

    @Test
    @DisplayName("Should keep valid values untouched and stay silent")
    void shouldKeepValidValues() {
        DispatchTiming timing = DispatchTiming.from(config(Duration.ofSeconds(5), Duration.ofMinutes(1)), logger);

        assertEquals(Duration.ofSeconds(5), timing.initialDelay());
        assertEquals(Duration.ofMinutes(1), timing.period());

        verifyNoInteractions(logger);
    }

    @Test
    @DisplayName("Should fall back to the default period when it is zero or negative")
    void shouldFallBackOnNonPositivePeriod() {
        DispatchTiming zero = DispatchTiming.from(config(Duration.ZERO, Duration.ZERO), logger);
        DispatchTiming negative = DispatchTiming.from(config(Duration.ZERO, Duration.ofSeconds(-30)), logger);

        assertEquals(DispatchTiming.DEFAULT_PERIOD, zero.period());
        assertEquals(DispatchTiming.DEFAULT_PERIOD, negative.period());

        assertFalse(mockingDetails(logger).getInvocations().isEmpty(), "expected a warning about the invalid period");
    }

    @Test
    @DisplayName("Should raise a period shorter than one tick to the minimum")
    void shouldRaiseSubTickPeriod() {
        DispatchTiming timing = DispatchTiming.from(config(Duration.ZERO, Duration.ofMillis(20)), logger);

        assertEquals(DispatchTiming.MINIMUM_PERIOD, timing.period());
    }

    @Test
    @DisplayName("Should replace a missing or negative initial delay with zero")
    void shouldNormalizeInitialDelay() {
        assertEquals(Duration.ZERO, DispatchTiming.from(config(null, Duration.ofSeconds(10)), logger).initialDelay());
        assertEquals(
                Duration.ZERO,
                DispatchTiming.from(config(Duration.ofSeconds(-1), Duration.ofSeconds(10)), logger).initialDelay()
        );
    }

    @Test
    @DisplayName("Should fall back to the default period when it is missing")
    void shouldFallBackOnMissingPeriod() {
        DispatchTiming timing = DispatchTiming.from(config(Duration.ZERO, null), logger);

        assertEquals(DispatchTiming.DEFAULT_PERIOD, timing.period());
    }

    @Test
    @DisplayName("Should reject values the scheduler cannot honour when constructed directly")
    void shouldRejectInvalidValues() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new DispatchTiming(Duration.ofSeconds(-1), Duration.ofSeconds(10))
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> new DispatchTiming(Duration.ZERO, Duration.ofMillis(10))
        );
    }
}
