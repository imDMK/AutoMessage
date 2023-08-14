package com.github.imdmk.automessage;

import com.github.imdmk.automessage.util.DurationUtil;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DurationUtilTest {

    static List<Object[]> durations() {
        return List.of(new Object[][] {
                { 0, Duration.ZERO },
                { 1, Duration.ofMillis(50) },
                { 20, Duration.ofSeconds(1) },
                { 1200, Duration.ofMinutes(1) },
                { 72000, Duration.ofHours(1) }
        });
    }

    @ParameterizedTest
    @MethodSource("durations")
    void toTickTest(long expected, Duration duration) {
        long result = DurationUtil.toTicks(duration);
        assertEquals(expected, result);
    }
}
