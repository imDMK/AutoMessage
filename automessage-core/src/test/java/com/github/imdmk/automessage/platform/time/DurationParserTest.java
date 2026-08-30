package com.github.imdmk.automessage.platform.time;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DurationParserTest {

    @ParameterizedTest(name = "\"{0}\" is {1} ms")
    @CsvSource({
            "500ms, 500",
            "30s,   30000",
            "5m,    300000",
            "2h,    7200000",
            "1d,    86400000",
            "1m30s, 90000",
            "1h30m, 5400000",
    })
    @DisplayName("reads the units the configuration documents")
    void parsesDocumentedUnits(String value, long expectedMillis) {
        assertThat(DurationParser.parse(value)).isEqualTo(Duration.ofMillis(expectedMillis));
    }

    @Test
    @DisplayName("reads a bare number as seconds, matching the dispatcher config")
    void bareNumberIsSeconds() {
        assertThat(DurationParser.parse("10")).isEqualTo(Duration.ofSeconds(10));
    }

    @Test
    @DisplayName("is case-insensitive and tolerates surrounding whitespace")
    void normalisesInput() {
        assertThat(DurationParser.parse("  1H30M ")).isEqualTo(Duration.ofMinutes(90));
    }

    @Test
    @DisplayName("keeps millisecond precision")
    void keepsMillis() {
        assertThat(DurationParser.parse("1s500ms")).isEqualTo(Duration.ofMillis(1_500));
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "   ", "abc", "10x", "5m junk", "junk5m"})
    @DisplayName("rejects anything that is not a duration")
    void rejectsGarbage(String value) {
        assertThatThrownBy(() -> DurationParser.parse(value))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("rejects null rather than parsing it as zero")
    void rejectsNull() {
        assertThatThrownBy(() -> DurationParser.parse(null))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
