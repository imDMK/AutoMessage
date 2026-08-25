package com.github.imdmk.automessage.scheduled.dispatcher;

import com.github.imdmk.automessage.config.ConfigManager;
import com.github.imdmk.automessage.platform.logger.PluginLogger;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

/**
 * Guards the meaning of the time values in {@code messagesDispatcher.yml}.
 *
 * @see <a href="https://github.com/imDMK/AutoMessage/issues/101">GH-101</a>
 */
class MessageDispatcherConfigTest {

    private final PluginLogger logger = mock(PluginLogger.class);

    @TempDir
    Path dataFolder;

    private MessageDispatcherConfig load() {
        return new ConfigManager(logger, dataFolder.toFile()).create(MessageDispatcherConfig.class);
    }

    private void write(String content) throws IOException {
        Files.writeString(dataFolder.resolve("messagesDispatcher.yml"), content);
    }

    private String read() throws IOException {
        return Files.readString(dataFolder.resolve("messagesDispatcher.yml"));
    }

    @Test
    @DisplayName("Should write durations with an explicit unit so the file is self-explanatory")
    void shouldWriteDurationsWithUnit() throws IOException {
        load();

        String content = read();

        assertTrue(content.contains("period: 10s"), () -> "expected 'period: 10s' in:\n" + content);
        assertTrue(content.contains("initialDelay: 10s"), () -> "expected 'initialDelay: 10s' in:\n" + content);
    }

    @Test
    @DisplayName("Should read a plain number as seconds")
    void shouldReadPlainNumberAsSeconds() throws IOException {
        write("""
                enabled: true
                period: 10
                initialDelay: 30
                selector: SEQUENTIAL
                """);

        MessageDispatcherConfig config = load();

        assertEquals(Duration.ofSeconds(10), config.period);
        assertEquals(Duration.ofSeconds(30), config.initialDelay);
    }

    @Test
    @DisplayName("Should rewrite a unit-less value with its unit on load")
    void shouldRewritePlainNumberWithUnit() throws IOException {
        write("""
                enabled: true
                period: 10
                initialDelay: 30
                selector: SEQUENTIAL
                """);

        load();

        String content = read();

        assertTrue(content.contains("period: 10s"), () -> "expected 'period: 10s' in:\n" + content);
        assertTrue(content.contains("initialDelay: 30s"), () -> "expected 'initialDelay: 30s' in:\n" + content);
    }

    @Test
    @DisplayName("Should support every documented time unit")
    void shouldSupportDocumentedUnits() throws IOException {
        write("""
                enabled: true
                period: 1m30s
                initialDelay: 500ms
                selector: RANDOM
                """);

        MessageDispatcherConfig config = load();

        assertEquals(Duration.ofSeconds(90), config.period);
        assertEquals(Duration.ofMillis(500), config.initialDelay);
    }

    @Test
    @DisplayName("Should be created in the configured data folder")
    void shouldUseConfiguredFileName() {
        MessageDispatcherConfig config = load();

        assertEquals("messagesDispatcher.yml", config.getFileName());
        assertTrue(new File(dataFolder.toFile(), config.getFileName()).isFile());
    }
}
