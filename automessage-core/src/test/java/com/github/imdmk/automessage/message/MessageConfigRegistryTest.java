package com.github.imdmk.automessage.message;

import com.eternalcode.multification.notice.Notice;
import com.eternalcode.multification.notice.NoticePart;
import com.eternalcode.multification.notice.resolver.text.TextContent;
import com.github.imdmk.automessage.config.ConfigManager;
import com.github.imdmk.automessage.platform.logger.PluginLogger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.List;
import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class MessageConfigRegistryTest {

    @TempDir
    Path dataFolder;

    private MessageConfigRegistry registry;

    @BeforeEach
    void setUp() {
        registry = MessageConfigRegistry.load(
                new ConfigManager(mock(PluginLogger.class), dataFolder.toFile())
        );
    }

    @Test
    @DisplayName("ships English, Polish and German out of the box")
    void shipsThreeLanguages() {
        assertThat(registry.all()).hasSize(3);

        assertThat(dataFolder.resolve("messages.yml")).exists();
        assertThat(dataFolder.resolve("messages_pl.yml")).exists();
        assertThat(dataFolder.resolve("messages_de.yml")).exists();
    }

    @Test
    @DisplayName("gives each player the file matching their client language")
    void resolvesByLanguage() {
        assertThat(registry.provide(Locale.of("pl", "PL"))).isInstanceOf(PLMessageConfig.class);
        assertThat(registry.provide(Locale.of("de", "DE"))).isInstanceOf(DEMessageConfig.class);
        assertThat(registry.provide(Locale.ENGLISH)).isInstanceOf(ENMessageConfig.class);
    }

    @Test
    @DisplayName("falls back to English for a language nobody translated")
    void fallsBackToEnglish() {
        assertThat(registry.provide(Locale.JAPANESE)).isInstanceOf(ENMessageConfig.class);
        assertThat(registry.provide(Locale.of("fr", "CA"))).isInstanceOf(ENMessageConfig.class);
    }

    @Test
    @DisplayName("falls back to English for the console, which has no language")
    void fallsBackForNullLocale() {
        assertThat(registry.provide(null)).isInstanceOf(ENMessageConfig.class);
    }

    @Test
    @DisplayName("a translation actually differs from the English it starts from")
    void translationsAreTranslated() {
        MessageConfig english = registry.provide(Locale.ENGLISH);
        MessageConfig polish = registry.provide(Locale.of("pl"));
        MessageConfig german = registry.provide(Locale.of("de"));

        // Compared as text, not as objects: Notice has no equals(), so comparing instances
        // would pass even when the "translation" is the English string.
        assertThat(text(polish.reloadMessages().configReloadedSuccess()))
                .isNotEqualTo(text(english.reloadMessages().configReloadedSuccess()));
        assertThat(text(german.reloadMessages().configReloadedSuccess()))
                .isNotEqualTo(text(english.reloadMessages().configReloadedSuccess()));
        assertThat(text(polish.viewMessages().viewPlayerOnly()))
                .isNotEqualTo(text(german.viewMessages().viewPlayerOnly()));
    }

    @Test
    @DisplayName("every shipped message is translated in both languages")
    void nothingIsLeftInEnglish() {
        MessageConfig english = registry.provide(Locale.ENGLISH);

        for (MessageConfig translation : List.of(
                registry.provide(Locale.of("pl")),
                registry.provide(Locale.of("de"))
        )) {
            assertThat(text(translation.dispatcherMessages().dispatcherEnabled()))
                    .isNotEqualTo(text(english.dispatcherMessages().dispatcherEnabled()));
            assertThat(text(translation.dispatcherMessages().dispatcherDisabled()))
                    .isNotEqualTo(text(english.dispatcherMessages().dispatcherDisabled()));
            assertThat(text(translation.reloadMessages().configReloadFailed()))
                    .isNotEqualTo(text(english.reloadMessages().configReloadFailed()));
            assertThat(text(translation.viewMessages().messageNotFound()))
                    .isNotEqualTo(text(english.viewMessages().messageNotFound()));
            assertThat(text(translation.liteCommandsMessages().commandPermissionMissing()))
                    .isNotEqualTo(text(english.liteCommandsMessages().commandPermissionMissing()));
        }
    }

    private static String text(Notice notice) {
        StringBuilder joined = new StringBuilder();

        for (NoticePart<?> part : notice.parts()) {
            if (part.content() instanceof TextContent content) {
                joined.append(String.join(" ", content.contents()));
            }
        }

        return joined.toString();
    }
}
