package com.github.imdmk.automessage.language;

import com.github.imdmk.automessage.config.ConfigManager;
import com.github.imdmk.automessage.platform.logger.PluginLogger;
import com.github.imdmk.automessage.scheduled.ScheduledMessage;
import com.github.imdmk.automessage.scheduled.ScheduledMessagesConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class ShippedDefaultsConsistencyTest {

    @TempDir
    Path dataFolder;

    @Test
    @DisplayName("every shipped message has text in every shipped language")
    void everyMessageHasTextEverywhere() {
        ConfigManager configManager = new ConfigManager(mock(PluginLogger.class), dataFolder.toFile());

        ScheduledMessagesConfig messages = configManager.create(ScheduledMessagesConfig.class);
        LanguageRegistry languages = LanguageRegistry.load(configManager, mock(PluginLogger.class), () -> "en");

        assertThat(messages.messages).isNotEmpty();

        for (ScheduledMessage message : messages.messages) {
            for (LanguageConfig language : languages.all()) {
                assertThat(language.announcement(message.name()))
                        .withFailMessage(
                                "message '%s' from scheduledMessages.yml has no text in lang/%s.yml",
                                message.name(), language.code()
                        )
                        .isNotNull();
            }
        }
    }

    @Test
    @DisplayName("no language ships text for a message that does not exist")
    void noOrphanedText() {
        ConfigManager configManager = new ConfigManager(mock(PluginLogger.class), dataFolder.toFile());

        ScheduledMessagesConfig messages = configManager.create(ScheduledMessagesConfig.class);
        LanguageRegistry languages = LanguageRegistry.load(configManager, mock(PluginLogger.class), () -> "en");

        List<String> known = messages.messages.stream().map(ScheduledMessage::name).toList();

        for (LanguageConfig language : languages.all()) {
            assertThat(language.announcements.keySet())
                    .withFailMessage("lang/%s.yml has text for a message nobody schedules", language.code())
                    .allMatch(known::contains);
        }
    }
}
