package com.github.imdmk.automessage.scheduled.dispatcher;

import com.github.imdmk.automessage.config.ConfigManager;
import com.github.imdmk.automessage.platform.logger.PluginLogger;
import com.github.imdmk.automessage.notice.time.DurationFormatter;
import com.github.imdmk.automessage.scheduled.channel.AnnouncementChannel;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class MessageDispatcherConfigTest {

    private final PluginLogger logger = mock(PluginLogger.class);

    @TempDir
    Path dataFolder;

    private MessageDispatcherConfig load() {
        return new ConfigManager(logger, dataFolder.toFile()).create(MessageDispatcherConfig.class);
    }

    private void write(String content) throws IOException {
        Files.writeString(dataFolder.resolve("config.yml"), content);
    }

    private String read() throws IOException {
        return Files.readString(dataFolder.resolve("config.yml"));
    }

    @Test
    @DisplayName("ships one explicit default channel, with no settings living outside the list")
    void shipsOneExplicitChannel() {
        List<AnnouncementChannel> channels = load().channels();

        assertThat(channels).hasSize(1);
        assertThat(channels.getFirst().matches(AnnouncementChannel.DEFAULT_NAME)).isTrue();
        assertThat(channels.getFirst().enabled()).isTrue();
    }

    @Test
    @DisplayName("writes durations with an explicit unit so the file is self-explanatory")
    void writesDurationsWithUnit() throws IOException {
        AnnouncementChannel channel = load().channels().getFirst();

        String content = read();

        // Asserted against the values the config actually defaults to rather than against
        // literals, so tuning a default does not fail a test about formatting.
        assertThat(content).contains("period: " + DurationFormatter.format(channel.period()));
        assertThat(content).contains("initialDelay: " + DurationFormatter.format(channel.initialDelay()));
    }

    @Test
    @DisplayName("ships defaults a production server can leave alone")
    void shipsUsableDefaults() {
        AnnouncementChannel channel = load().channels().getFirst();

        // Anything under a minute between announcements reads as spam to players, and the
        // shipped file is what most servers will run unchanged.
        assertThat(channel.period()).isGreaterThanOrEqualTo(Duration.ofMinutes(1));
        assertThat(channel.initialDelay()).isGreaterThan(Duration.ZERO);
    }

    @Test
    @DisplayName("reads a channel written by hand, including a plain number as seconds")
    void readsHandWrittenChannels() throws IOException {
        write("""
                enabled: true
                fallbackLanguage: en
                channels:
                  - name: ads
                    enabled: true
                    initialDelay: 30
                    period: 1m30s
                    selector: SHUFFLE
                """);

        List<AnnouncementChannel> channels = load().channels();

        assertThat(channels).hasSize(1);
        assertThat(channels.getFirst().name()).isEqualTo("ads");
        assertThat(channels.getFirst().initialDelay()).isEqualTo(Duration.ofSeconds(30));
        assertThat(channels.getFirst().period()).isEqualTo(Duration.ofSeconds(90));
    }

    @Test
    @DisplayName("rewrites a unit-less value with its unit on load")
    void rewritesPlainNumberWithUnit() throws IOException {
        write("""
                enabled: true
                fallbackLanguage: en
                channels:
                  - name: default
                    enabled: true
                    initialDelay: 30
                    period: 10
                    selector: SEQUENTIAL
                """);

        load();

        assertThat(read()).contains("period: 10s").contains("initialDelay: 30s");
    }

    @Test
    @DisplayName("names a fallback language but does not list which languages exist")
    void namesOnlyTheFallback() {
        MessageDispatcherConfig config = load();

        assertThat(config.fallbackLanguage).isEqualTo("en");
    }

    @Test
    @DisplayName("does not offer a setting for which languages exist - the lang/ folder decides")
    void doesNotListLanguages() throws IOException {
        load();

        // A list here would be a choice that changes nothing: files already on disk are found
        // whether or not they are named, and naming one nobody translated only produces a file
        // of English text under a foreign name.
        assertThat(read()).doesNotContain("languages:");
    }
}
