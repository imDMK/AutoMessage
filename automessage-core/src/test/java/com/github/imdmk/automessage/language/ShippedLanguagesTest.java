package com.github.imdmk.automessage.language;

import com.github.imdmk.automessage.config.ConfigManager;
import com.github.imdmk.automessage.notice.ChatPart;
import com.github.imdmk.automessage.notice.Notice;
import com.github.imdmk.automessage.platform.logger.PluginLogger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

// The shipped translations are handed to a record constructor by position, thirty-one strings
// long. Inserting a field and forgetting one list shifts every translation after it by one, and
// nothing else in the build would notice: it compiles, and every message still says something.
// These anchors are spread across the record so a shift anywhere lands on at least one.
class ShippedLanguagesTest {

    @TempDir
    Path dataFolder;

    private LanguageRegistry languages;

    @BeforeEach
    void setUp() {
        this.languages = LanguageRegistry.load(
                new ConfigManager(mock(PluginLogger.class), dataFolder.toFile()),
                mock(PluginLogger.class),
                () -> "en"
        );
    }

    private String text(String code, Function<CommandMessages, Notice> field) {
        final Notice notice = field.apply(languages.provide(code).commands);
        return ((ChatPart) notice.parts().getFirst()).lines().getFirst();
    }

    @Test
    @DisplayName("Polish messages land on the fields they were written for")
    void polishIsNotShifted() {
        assertThat(text("pl", commands -> commands.viewPlayerOnly)).contains("Tylko gracze");
        assertThat(text("pl", commands -> commands.nextDisabled)).contains("wyłączony");
        assertThat(text("pl", commands -> commands.statsChannelsHeader)).contains("Kanały");
        assertThat(text("pl", commands -> commands.statsMessagesHeader)).contains("Wiadomości");
        assertThat(text("pl", commands -> commands.statsChannelDisabled)).contains("wyłączony w config.yml");
        assertThat(text("pl", commands -> commands.statsChannelEmpty)).contains("brak przypisanych");
        assertThat(text("pl", commands -> commands.channelNotFound)).contains("nie istnieje");
        assertThat(text("pl", commands -> commands.toggleOff)).contains("wyłączone");
        assertThat(text("pl", commands -> commands.togglePlayerOnly)).contains("Tylko gracze");
    }

    @Test
    @DisplayName("German messages land on the fields they were written for")
    void germanIsNotShifted() {
        assertThat(text("de", commands -> commands.viewPlayerOnly)).contains("Nur Spieler");
        assertThat(text("de", commands -> commands.nextDisabled)).contains("deaktiviert");
        assertThat(text("de", commands -> commands.statsChannelsHeader)).contains("Kanäle");
        assertThat(text("de", commands -> commands.statsMessagesHeader)).contains("Nachrichten");
        assertThat(text("de", commands -> commands.statsChannelDisabled)).contains("deaktiviert");
        assertThat(text("de", commands -> commands.statsChannelEmpty)).contains("keine Nachrichten");
        assertThat(text("de", commands -> commands.channelNotFound)).contains("keinen Kanal");
        assertThat(text("de", commands -> commands.toggleOff)).contains("aus");
        assertThat(text("de", commands -> commands.togglePlayerOnly)).contains("Nur Spieler");
    }

    @Test
    @DisplayName("a placeholder a message needs is present in every language that ships it")
    void placeholdersSurviveTranslation() {
        // A translation that drops {DELAY} silently removes the answer the command exists to give.
        for (final String code : new String[] {"en", "pl", "de"}) {
            assertThat(text(code, commands -> commands.nextEntry))
                    .withFailMessage("nextEntry in %s lost a placeholder", code)
                    .contains("{CHANNEL}", "{MESSAGE}", "{DELAY}");
            // No {AGO}: when a channel last fired is dropped from its row on purpose, because
            // the row has to fit a chat line. The message rows underneath still carry it.
            assertThat(text(code, commands -> commands.statsChannel))
                    .withFailMessage("statsChannel in %s lost a placeholder", code)
                    .contains("{CHANNEL}", "{COUNT}", "{DELAY}");
            assertThat(text(code, commands -> commands.statsEntry))
                    .withFailMessage("statsEntry in %s lost a placeholder", code)
                    .contains("{MESSAGE}", "{CHANNEL}", "{COUNT}", "{AGO}");
        }
    }
}
