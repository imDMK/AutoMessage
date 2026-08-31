package com.github.imdmk.automessage.scheduled.trigger;

import com.github.imdmk.automessage.config.ConfigManager;
import com.github.imdmk.automessage.platform.logger.PluginLogger;
import com.github.imdmk.automessage.scheduled.ScheduledMessage;
import com.github.imdmk.automessage.scheduled.ScheduledMessageBuilder;
import com.github.imdmk.automessage.scheduled.ScheduledMessagesConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;

/** Round-trips every trigger shape through the real configuration machinery. */
class MessageTriggerSerializerTest {

    @TempDir
    Path dataFolder;

    private ScheduledMessagesConfig write(List<ScheduledMessage> messages) {
        ConfigManager manager = new ConfigManager(mock(PluginLogger.class), dataFolder.toFile());
        ScheduledMessagesConfig config = manager.create(ScheduledMessagesConfig.class);

        config.messages = messages;
        manager.saveAll();

        return config;
    }

    private List<ScheduledMessage> reload() {
        return new ConfigManager(mock(PluginLogger.class), dataFolder.toFile())
                .create(ScheduledMessagesConfig.class)
                .messages;
    }

    /** @return the file below its comment header, so assertions cannot match the documentation */
    private String dataSection() throws Exception {
        String content = Files.readString(dataFolder.resolve("scheduledMessages.yml"));

        return content.substring(content.indexOf("\nmessages:"));
    }

    private static ScheduledMessage triggered(String name, MessageTrigger trigger) {
        return ScheduledMessageBuilder.create().name(name).trigger(trigger).build();
    }

    @Test
    @DisplayName("round-trips every trigger shape unchanged")
    void roundTripsEveryShape() {
        List<ScheduledMessage> original = List.of(
                triggered("join-immediate", MessageTrigger.join(Duration.ZERO)),
                triggered("join-delayed", MessageTrigger.join(Duration.ofSeconds(3))),
                triggered("join-compound", MessageTrigger.join(Duration.ofMinutes(1).plusSeconds(30))),
                triggered("join-millis", MessageTrigger.join(Duration.ofMillis(500))),
                triggered("first-join", MessageTrigger.firstJoin(Duration.ofSeconds(5))),
                triggered("first-join-immediate", MessageTrigger.firstJoin(Duration.ZERO)),
                triggered("milestone", MessageTrigger.playerCount(100))
        );

        write(original);

        assertThat(reload()).isEqualTo(original);
    }

    @Test
    @DisplayName("a JOIN trigger keeps its kind, which is derived rather than stored")
    void keepsTheJoinKind() {
        write(List.of(
                triggered("plain", MessageTrigger.join(Duration.ofSeconds(3))),
                triggered("first", MessageTrigger.firstJoin(Duration.ofSeconds(3)))
        ));

        List<ScheduledMessage> reloaded = reload();

        assertThat(reloaded.get(0).trigger()).isInstanceOf(JoinTrigger.class);
        assertThat(((JoinTrigger) reloaded.get(0).trigger()).firstJoinOnly()).isFalse();
        assertThat(reloaded.get(1).trigger().type()).isEqualTo(MessageTrigger.Type.FIRST_JOIN);
        assertThat(((JoinTrigger) reloaded.get(1).trigger()).firstJoinOnly()).isTrue();
    }

    @Test
    @DisplayName("omits a zero delay from the file and reads it back as zero")
    void omitsZeroDelay() throws Exception {
        write(List.of(triggered("join-immediate", MessageTrigger.join(Duration.ZERO))));

        // Scoped to the data: the header documents a 'delay: 3s' example, so searching the
        // whole file would find that instead.
        String data = dataSection();

        assertThat(data).contains("type: JOIN").doesNotContain("delay:");
        assertThat(((JoinTrigger) reload().getFirst().trigger()).delay()).isEqualTo(Duration.ZERO);
    }

    @Test
    @DisplayName("writes the delay in the notation the config documents")
    void writesReadableDelay() throws Exception {
        write(List.of(triggered("join", MessageTrigger.join(Duration.ofMinutes(1).plusSeconds(30)))));

        assertThat(dataSection()).contains("delay: 1m30s");
    }

    @Test
    @DisplayName("reads a trigger written by hand")
    void readsHandWrittenTriggers() throws Exception {
        Files.writeString(dataFolder.resolve("scheduledMessages.yml"), """
                messages:
                - name: welcome
                  trigger:
                    type: FIRST_JOIN
                    delay: 10
                - name: milestone
                  trigger:
                    type: PLAYER_COUNT
                    threshold: 250
                """);

        List<ScheduledMessage> messages = reload();

        assertThat(((JoinTrigger) messages.get(0).trigger()).delay()).isEqualTo(Duration.ofSeconds(10));
        assertThat(((PlayerCountTrigger) messages.get(1).trigger()).threshold()).isEqualTo(250);
    }

    @Test
    @DisplayName("says which field is missing instead of failing on okaeri internals")
    void reportsMissingRequiredFields() throws Exception {
        Files.writeString(dataFolder.resolve("scheduledMessages.yml"), """
                messages:
                - name: m
                  trigger:
                    type: PLAYER_COUNT
                """);

        assertThatThrownBy(this::reload)
                .rootCause()
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("threshold");

        Files.writeString(dataFolder.resolve("scheduledMessages.yml"), """
                messages:
                - name: m
                  trigger:
                    delay: 3s
                """);

        assertThatThrownBy(this::reload)
                .rootCause()
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("type");
    }

    @Test
    @DisplayName("names the valid values when the type is misspelled")
    void reportsUnknownType() throws Exception {
        Files.writeString(dataFolder.resolve("scheduledMessages.yml"), """
                messages:
                - name: m
                  trigger:
                    type: FIRSTJOIN
                """);

        assertThatThrownBy(this::reload)
                .rootCause()
                .hasMessageContaining("FIRST_JOIN");
    }

    @Test
    @DisplayName("rejects a duration it cannot read, naming what it saw")
    void reportsUnreadableDuration() throws Exception {
        Files.writeString(dataFolder.resolve("scheduledMessages.yml"), """
                messages:
                - name: m
                  trigger:
                    type: JOIN
                    delay: soon
                """);

        assertThatThrownBy(this::reload)
                .rootCause()
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("soon");
    }
}
