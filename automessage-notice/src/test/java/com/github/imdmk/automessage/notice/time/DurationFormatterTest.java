package com.github.imdmk.automessage.notice.time;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DurationFormatterTest {

    @Test
    @DisplayName("Should format durations using the notation accepted by the config files")
    void shouldFormatUsingConfigNotation() {
        assertEquals("10s", DurationFormatter.format(Duration.ofSeconds(10)));
        assertEquals("5m", DurationFormatter.format(Duration.ofMinutes(5)));
        assertEquals("1h", DurationFormatter.format(Duration.ofHours(1)));
        assertEquals("500ms", DurationFormatter.format(Duration.ofMillis(500)));
        assertEquals("1m30s", DurationFormatter.format(Duration.ofSeconds(90)));
    }

    @Test
    @DisplayName("Should format zero and negative durations")
    void shouldFormatZeroAndNegative() {
        assertEquals("0s", DurationFormatter.format(Duration.ZERO));
        assertEquals("-5s", DurationFormatter.format(Duration.ofSeconds(-5)));
    }

    @Test
    @DisplayName("Should describe a missing duration instead of throwing")
    void shouldDescribeMissingDuration() {
        assertEquals("unset", DurationFormatter.format(null));
    }

    @Test
    @DisplayName("a countdown is rounded up to the whole second a person can read")
    void countdownRoundsUpToSeconds() {
        // Up rather than down: "9s487ms" is noise on a line somebody reads, and a countdown must
        // never claim less time than is left, nor say "0s" while there is any.
        assertEquals("10s", DurationFormatter.formatCountdown(Duration.ofMillis(9_487)));
        assertEquals("1s", DurationFormatter.formatCountdown(Duration.ofMillis(400)));
        assertEquals("5m", DurationFormatter.formatCountdown(Duration.ofMinutes(5)));
        assertEquals("0s", DurationFormatter.formatCountdown(Duration.ZERO));
        assertEquals("unset", DurationFormatter.formatCountdown(null));
    }
}
