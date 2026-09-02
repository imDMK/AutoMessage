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
    @DisplayName("written for a person: whole seconds, spaced units, no milliseconds")
    void readableDropsMillisAndSpacesUnits() {
        assertEquals("34s", DurationFormatter.formatReadable(Duration.ofMillis(34_572)));
        assertEquals("1h 34s", DurationFormatter.formatReadable(Duration.ofSeconds(3_634)));
        assertEquals("1h 2m 3s", DurationFormatter.formatReadable(Duration.ofSeconds(3_723)));
        assertEquals("5m", DurationFormatter.formatReadable(Duration.ofMinutes(5)));
    }

    @Test
    @DisplayName("a fraction of a second left still reads as a second, never as none")
    void readableNeverRoundsAwayARemainder() {
        // "next in 0s" on a channel that has not fired yet reads as broken.
        assertEquals("1s", DurationFormatter.formatReadable(Duration.ofMillis(400)));
        assertEquals("1s", DurationFormatter.formatReadable(Duration.ofMillis(1)));
        assertEquals("0s", DurationFormatter.formatReadable(Duration.ZERO));
        assertEquals("unset", DurationFormatter.formatReadable(null));
    }

    @Test
    @DisplayName("the configuration notation keeps its milliseconds and stays unspaced")
    void configNotationIsUntouched() {
        // format() is what gets written into config.yml and read back by the parser; a space or
        // a dropped unit there would change files on disk, not just a line in chat.
        assertEquals("500ms", DurationFormatter.format(Duration.ofMillis(500)));
        assertEquals("1m30s", DurationFormatter.format(Duration.ofSeconds(90)));
        assertEquals("1s500ms", DurationFormatter.format(Duration.ofMillis(1_500)));
    }
}
