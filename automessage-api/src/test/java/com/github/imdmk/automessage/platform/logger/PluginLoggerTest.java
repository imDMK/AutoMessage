package com.github.imdmk.automessage.platform.logger;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class PluginLoggerTest {

    private record Entry(PluginLogger.Level level, String message, Throwable throwable) {
    }

    private final List<Entry> written = new ArrayList<>();

    private final PluginLogger logger = (level, message, throwable) ->
            written.add(new Entry(level, message, throwable));

    @Test
    @DisplayName("should route each level to the one method a platform implements")
    void shouldRouteEveryLevel() {
        logger.info("started");
        logger.warn("odd");
        logger.error("broken");

        assertThat(written).extracting(Entry::level).containsExactly(
                PluginLogger.Level.INFO,
                PluginLogger.Level.WARN,
                PluginLogger.Level.ERROR
        );
    }

    @Test
    @DisplayName("should format arguments the same way on every platform")
    void shouldFormatArguments() {
        logger.info("Loaded %d language(s): %s.", 2, "en, pl");

        assertThat(written).singleElement()
                .extracting(Entry::message)
                .isEqualTo("Loaded 2 language(s): en, pl.");
    }

    @Test
    @DisplayName("should leave a message with no arguments exactly as written")
    void shouldNotFormatWithoutArguments() {
        // Collapsing the overloads means a call with no arguments now reaches the formatter,
        // where a stray percent sign would be read as a specifier and throw.
        logger.warn("Channel 'sale 50% off' is not configured.");

        assertThat(written).singleElement()
                .extracting(Entry::message)
                .isEqualTo("Channel 'sale 50% off' is not configured.");
    }

    @Test
    @DisplayName("should carry the cause through to the platform")
    void shouldCarryTheCause() {
        final Throwable cause = new IllegalStateException("boom");

        logger.error(cause, "Failed to reload %s.", "config.yml");

        assertThat(written).singleElement().satisfies(entry -> {
            assertThat(entry.throwable()).isSameAs(cause);
            assertThat(entry.message()).isEqualTo("Failed to reload config.yml.");
        });
    }

    @Test
    @DisplayName("should report no cause when there is none")
    void shouldReportNoCause() {
        logger.warn("just a warning");

        assertThat(written).singleElement().extracting(Entry::throwable).isNull();
    }
}
