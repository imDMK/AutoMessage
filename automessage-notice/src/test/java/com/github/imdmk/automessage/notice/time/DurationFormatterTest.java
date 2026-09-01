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
}
